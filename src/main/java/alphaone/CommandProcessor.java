package alphaone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
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
    private static final int SINGLE_WORD_COMMAND_LENGTH = 1;
    private static final int TWO_WORD_COMMAND_LENGTH = 2;

    private final TaskList taskList;
    private final Storage storage;
    private final ContactList contactList;
    private boolean isExitRequested = false;

    // Small functional interface for handlers that may throw the checked exceptions
    private interface CommandHandler {
        void handle(String[] tokens, String commandWord)
                throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException;
    }

    private final Map<String, CommandHandler> handlers = new HashMap<>();

    /**
     * Create a new CommandProcessor.
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
        } catch (InvalidCommandException | IncompleteDetailsException | InvalidDateTimeException exception) {
            Ui.print(exception.getMessage());
        }
    }

    // Dispatch using the handlers map; single-level orchestrator.
    private void dispatch(String commandWord, String[] tokens)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        CommandHandler handler = handlers.get(commandWord);
        if (handler == null) {
            throw new InvalidCommandException();
        }
        handler.handle(tokens, commandWord);
    }

    // ---------- Contact handling ----------

    private void handleContact(String[] tokens) throws InvalidCommandException {
        if (tokens.length < 2) {
            throw new InvalidCommandException();
        }
        String action = tokens[1].toLowerCase();
        switch (action) {
        case "add" -> handleContactAdd(tokens);
        case "remove" -> handleContactRemove(tokens);
        case "list" -> handleContactList();
        default -> throw new InvalidCommandException();
        }
    }

    private void handleContactAdd(String[] tokens) throws InvalidCommandException {
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(tokens));
        arguments.remove(0); // remove "contact"
        arguments.remove(0); // remove "add"
        validateCommandLength(arguments.size(), AlphaOne.CommandType.CONTACT_ADD);
        String phone = arguments.get(arguments.size() - 1);
        String name;
        if (arguments.size() == 2) {
            name = arguments.get(0);
        } else {
            List<String> nameParts = arguments.subList(0, arguments.size() - 1);
            name = String.join(" ", nameParts);
        }
        Contact newContact = new Contact(name, phone);
        contactList.addContact(newContact);
        Ui.print("New contact added:\n" + String.format("%s (%s)", newContact.getName(), phone));
    }

    private void handleContactRemove(String[] tokens) throws InvalidCommandException {
        ArrayList<String> removeArguments = new ArrayList<>(Arrays.asList(tokens));
        removeArguments.remove(0); // remove "contact"
        removeArguments.remove(0); // remove "remove"
        validateCommandLength(removeArguments.size(), AlphaOne.CommandType.CONTACT_DELETE);
        String targetName = String.join(" ", removeArguments);
        Contact removedContact = contactList.removeContactByName(targetName);
        if (removedContact == null) {
            Ui.print("No contact found with that name.");
            return;
        }
        Ui.print("Contact removed:\n" + String.format("%s (%s)", removedContact.getName(), removedContact.getPhone()));
    }

    private void handleContactList() {
        Ui.print(contactList.formatContactsDisplay());
    }

    // ---------- Command handlers ----------

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
        default -> AlphaOne.CommandType.DELETE;
        };
        validateCommandLength(tokens.length, commandType);
        try {
            int taskNumber = Integer.parseInt(tokens[1]);
            taskList.verifyTaskExists(taskNumber);
            switch (commandWord) {
            case "mark" -> Ui.print(taskList.buildMarkDoneMessage(taskNumber));
            case "unmark" -> Ui.print(taskList.buildUnmarkDoneMessage(taskNumber));
            default -> Ui.print(taskList.buildDeleteTaskMessage(taskNumber));
            }
        } catch (InvalidTaskItemException invalidTaskItemException) {
            Ui.print(invalidTaskItemException.getMessage());
        } catch (Exception exception) {
            Ui.print("Invalid task number!");
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

    // ---------- Small helpers ----------

    /**
     * Validates that the actual token count matches what the given command type expects.
     * Throws InvalidCommandException with a descriptive message if they do not match.
     */
    private void validateCommandLength(int actual, AlphaOne.CommandType type) throws InvalidCommandException {
        int expectedLength = switch (type) {
        case BYE, LIST, CONTACT_DELETE -> SINGLE_WORD_COMMAND_LENGTH;
        case MARK, UNMARK, DELETE, FIND, CONTACT_ADD -> TWO_WORD_COMMAND_LENGTH;
        default -> throw new InvalidCommandException();
        };
        if (expectedLength != actual) {
            throw new InvalidCommandException(type);
        }
    }

    private String extractTodoDescription(String[] tokens) {
        List<String> tokenList = new ArrayList<>(Arrays.asList(tokens));
        tokenList.remove(0);
        return String.join(" ", tokenList);
    }
}
