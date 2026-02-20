package alphaone.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidContactException;
import alphaone.exception.InvalidDateTimeException;
import alphaone.exception.InvalidTaskItemException;
import alphaone.model.Contact;
import alphaone.model.ContactList;
import alphaone.model.TaskList;
import alphaone.parser.Parser;
import alphaone.storage.Storage;
import alphaone.ui.Ui;

/**
 * Encapsulates parsing and execution of textual commands so CLI and GUI can
 * share the same logic.
 */
public class CommandProcessor {
    private static final int EXACT_ONE_TOKEN = 1;
    private static final int EXACT_TWO_TOKENS = 2;
    // Minimum argument count after stripping "contact add": at least one name word + phone
    private static final int MIN_CONTACT_ADD_ARGS = 2;

    private interface CommandHandler {
        void handle(String[] tokens, String commandWord)
                throws InvalidCommandException, IncompleteDetailsException,
                       InvalidDateTimeException, InvalidContactException;
    }

    private final TaskList taskList;
    private final Storage storage;
    private final ContactList contactList;
    private boolean isExitRequested = false;

    private final Map<String, CommandHandler> handlers = new HashMap<>();

    /**
     * Creates a new CommandProcessor.
     *
     * @param taskList    the application's TaskList
     * @param storage     the Storage used to persist tasks
     * @param contactList the application's ContactList
     */
    public CommandProcessor(TaskList taskList, Storage storage, ContactList contactList) {
        this.taskList = taskList;
        this.storage = storage;
        this.contactList = contactList;

        handlers.put("contact", (tokens, cmd) -> handleContact(tokens));
        handlers.put("bye", (tokens, cmd) -> handleBye(tokens));
        handlers.put("list", (tokens, cmd) -> handleList(tokens));
        handlers.put("mark", (tokens, cmd) -> handleTaskModification(tokens, "mark"));
        handlers.put("unmark", (tokens, cmd) -> handleTaskModification(tokens, "unmark"));
        handlers.put("delete", (tokens, cmd) -> handleTaskModification(tokens, "delete"));
        handlers.put("find", (tokens, cmd) -> handleFind(tokens));
        handlers.put("todo", (tokens, cmd) -> handleTodo(tokens));
        handlers.put("deadline", (tokens, cmd) -> handleDeadline(tokens));
        handlers.put("event", (tokens, cmd) -> handleEvent(tokens));
    }

    /**
     * Returns true if an exit command was processed.
     *
     * @return true when the last processed command was exit
     */
    public boolean isExit() {
        return isExitRequested;
    }

    /**
     * Processes a textual user input and prints the response via Ui.
     *
     * @param input user input line
     */
    public void process(String input) {
        String[] tokens = Parser.splitInput(input);
        String commandWord = tokens.length > 0 ? tokens[0].toLowerCase() : "";
        try {
            dispatch(commandWord, tokens);
        } catch (InvalidCommandException | IncompleteDetailsException
                | InvalidDateTimeException | InvalidContactException exception) {
            Ui.print(exception.getMessage());
        }
    }

    private void dispatch(String commandWord, String[] tokens)
            throws InvalidCommandException, IncompleteDetailsException,
                   InvalidDateTimeException, InvalidContactException {
        CommandHandler handler = handlers.get(commandWord);
        if (handler == null) {
            throw new InvalidCommandException();
        }
        handler.handle(tokens, commandWord);
    }

    private void handleContact(String[] tokens) throws InvalidCommandException, InvalidContactException {
        if (tokens.length < 2) {
            throw new InvalidCommandException(AlphaOne.CommandType.CONTACT);
        }
        String action = tokens[1].toLowerCase();
        switch (action) {
        case "add" -> handleContactAdd(tokens);
        case "remove" -> handleContactRemove(tokens);
        case "list" -> handleContactList(tokens);
        default -> throw new InvalidCommandException(AlphaOne.CommandType.CONTACT);
        }
    }

    private void handleContactAdd(String[] tokens) throws InvalidCommandException, InvalidContactException {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(tokens));
        arguments.remove(0);
        arguments.remove(0);

        if (arguments.size() < MIN_CONTACT_ADD_ARGS) {
            throw new InvalidCommandException(AlphaOne.CommandType.CONTACT_ADD);
        }

        String phone = arguments.get(arguments.size() - 1);
        String name = arguments.size() == MIN_CONTACT_ADD_ARGS
                ? arguments.get(0)
                : String.join(" ", arguments.subList(0, arguments.size() - 1));

        Contact newContact = Contact.of(name, phone);
        contactList.addContact(newContact);
        Ui.print("New contact added:\n" + newContact.getName() + " (" + newContact.getPhone() + ")");
    }

    private void handleContactRemove(String[] tokens) throws InvalidContactException {
        ArrayList<String> nameTokens = new ArrayList<>(Arrays.asList(tokens));
        nameTokens.remove(0);
        nameTokens.remove(0);

        String targetName = String.join(" ", nameTokens).trim();
        if (targetName.isEmpty()) {
            throw new InvalidContactException(InvalidContactException.Reason.EMPTY_NAME);
        }

        Contact removedContact = contactList.removeContactByName(targetName);
        Ui.print("Contact removed:\n" + removedContact.getName() + " (" + removedContact.getPhone() + ")");
    }

    private void handleContactList(String[] tokens) throws InvalidCommandException {
        if (tokens.length != 2) {
            throw new InvalidCommandException(AlphaOne.CommandType.CONTACT);
        }
        Ui.print(contactList.formatContactsDisplay());
    }

    private void handleBye(String[] tokens) throws InvalidCommandException {
        validateCommandLength(tokens.length, AlphaOne.CommandType.BYE);
        storage.save(taskList.getInternalMap());
        isExitRequested = true;
        Ui.print("Thank you for using AlphaOne! ");
    }

    private void handleList(String[] tokens) throws InvalidCommandException {
        validateCommandLength(tokens.length, AlphaOne.CommandType.LIST);
        taskList.printTasks();
    }

    /**
     * Handles mark/unmark/delete which all require parsing a task index.
     */
    private void handleTaskModification(String[] tokens, String commandWord) throws InvalidCommandException {
        AlphaOne.CommandType commandType = switch (commandWord) {
        case "mark" -> AlphaOne.CommandType.MARK;
        case "unmark" -> AlphaOne.CommandType.UNMARK;
        case "delete" -> AlphaOne.CommandType.DELETE;
        default -> throw new InvalidCommandException();
        };
        validateCommandLength(tokens.length, commandType);
        try {
            int taskNumber = Integer.parseInt(tokens[1]);
            taskList.verifyTaskExists(taskNumber);
            executeTaskModification(taskNumber, commandWord);
        } catch (InvalidTaskItemException invalidTaskItemException) {
            Ui.print(invalidTaskItemException.getMessage());
        } catch (InvalidCommandException invalidCommandException) {
            throw invalidCommandException;
        } catch (Exception exception) {
            Ui.print("Invalid task number!");
        }
    }

    /**
     * Executes the mark, unmark, or delete operation for a verified task number.
     *
     * @param taskNumber  the verified id of the task to operate on.
     * @param commandWord the operation to perform: "mark", "unmark", or "delete".
     * @throws InvalidCommandException if commandWord is not a recognised operation.
     */
    private void executeTaskModification(int taskNumber, String commandWord) throws InvalidCommandException {
        switch (commandWord) {
        case "mark" -> Ui.print(taskList.buildMarkDoneMessage(taskNumber));
        case "unmark" -> Ui.print(taskList.buildUnmarkDoneMessage(taskNumber));
        case "delete" -> Ui.print(taskList.buildDeleteTaskMessage(taskNumber));
        default -> throw new InvalidCommandException();
        }
    }

    private void handleFind(String[] tokens) throws InvalidCommandException {
        validateCommandLength(tokens.length, AlphaOne.CommandType.FIND);
        String keyword = Parser.joinFromIndex(tokens, 1);
        if (keyword.isEmpty()) {
            throw new InvalidCommandException(AlphaOne.CommandType.FIND);
        }
        Ui.print(taskList.buildSearchResultsMessage(keyword));
    }

    private void handleTodo(String[] tokens) throws IncompleteDetailsException {
        if (tokens.length < 2) {
            throw new IncompleteDetailsException(AlphaOne.TaskType.TODO);
        }
        Ui.print(taskList.buildAddTaskMessage(extractTodoDescription(tokens), AlphaOne.TaskType.TODO));
    }

    private void handleDeadline(String[] tokens)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        if (tokens.length < 2) {
            throw new InvalidCommandException(AlphaOne.TaskType.DEADLINE);
        }
        ArrayList<String> parsedParts = Parser.parseTaskArguments(tokens, AlphaOne.TaskType.DEADLINE);
        Ui.print(taskList.buildAddTaskMessage(parsedParts.get(0), AlphaOne.TaskType.DEADLINE, parsedParts.get(1)));
    }

    private void handleEvent(String[] tokens)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        ArrayList<String> parsedParts = Parser.parseTaskArguments(tokens, AlphaOne.TaskType.EVENT);
        Ui.print(taskList.buildAddTaskMessage(parsedParts.get(0), AlphaOne.TaskType.EVENT,
                parsedParts.get(1), parsedParts.get(2)));
    }


    /**
     * Validates that the actual token count satisfies the requirements of the given command type.
     * Commands with a fixed arity check for an exact match; contact commands check for a minimum.
     * Throws InvalidCommandException with a descriptive message if the check fails.
     */
    private void validateCommandLength(int actual, AlphaOne.CommandType type) throws InvalidCommandException {
        switch (type) {
        case BYE, LIST -> {
            if (actual != EXACT_ONE_TOKEN) {
                throw new InvalidCommandException(type);
            }
        }
        case MARK, UNMARK, DELETE, FIND -> {
            if (actual != EXACT_TWO_TOKENS) {
                throw new InvalidCommandException(type);
            }
        }
        default -> throw new InvalidCommandException(type);
        }
    }

    /**
     * Joins all tokens after the command word into the todo description string.
     *
     * @param tokens the raw token array, where index 0 is the command word.
     * @return the description text joined from index 1 onward.
     */
    private String extractTodoDescription(String[] tokens) {
        List<String> tokenList = new ArrayList<>(Arrays.asList(tokens));
        tokenList.remove(0);
        return String.join(" ", tokenList);
    }
}

