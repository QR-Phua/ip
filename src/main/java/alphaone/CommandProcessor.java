package alphaone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alphaone.exception.IncompleteDetailsException;
import alphaone.exception.InvalidCommandException;
import alphaone.exception.InvalidDateTimeException;
import alphaone.exception.InvalidTaskItemException;
import alphaone.model.Contact;
import alphaone.model.ContactList;
import alphaone.model.TaskList;
import alphaone.parser.Parser;
import alphaone.storage.Storage;

/**
 * Encapsulates parsing and execution of textual commands so CLI and GUI can
 * share the same logic.
 */
public class CommandProcessor {
    private final TaskList taskList;

    private final Storage storage;
    private final ContactList contactList;
    private boolean exit = false;

    /**
     * Create a new CommandProcessor.
     *
     * @param taskList the application's TaskList
     * @param storage  the Storage used to persist tasks
     * @param contactList the application's ContactList
     */
    public CommandProcessor(TaskList taskList, Storage storage, ContactList contactList) {
        this.taskList = taskList;
        this.storage = storage;
        this.contactList = contactList;
    }

    /**
     * Returns true if an exit command was processed.
     *
     * @return true when the last processed command was exit
     */
    public boolean isExit() {
        return exit;
    }

    /**
     * Process a textual user input and return the response string.
     *
     * @param input user input line
     * @return response text to display
     */
    public String process(String input) {
        String[] commands = Parser.splitInput(input);
        String cmd = commands.length > 0 ? commands[0].toLowerCase() : "";
        try {
            return switch (cmd) {
            case "contact" -> handleContact(commands);
            case "bye" -> handleBye(commands);
            case "list" -> handleList(commands);
            case "mark", "unmark", "delete" -> handleMutate(commands, cmd);
            case "find" -> handleFind(commands);
            case "todo" -> handleTodo(commands);
            case "deadline" -> handleDeadline(commands);
            case "event" -> handleEvent(commands);
            default -> throw new InvalidCommandException();
            };
        } catch (InvalidCommandException | IncompleteDetailsException | InvalidDateTimeException exe) {
            return exe.getMessage();
        }
    }

    private String handleContact(String[] commands) throws InvalidCommandException {
        if (commands.length < 2) {
            throw new InvalidCommandException();
        }
        String action = commands[1].toLowerCase();
        switch (action) {
        case "add":
            // gather the remaining tokens as arguments (name and phone)
            ArrayList<String> arguments = new ArrayList<>(Arrays.asList(commands));
            // remove "contact" and "add"
            arguments.remove(0);
            arguments.remove(0);
            // check expected arg count (name + phone)
            commandLengthChecker(arguments.size(), AlphaOne.CommandType.CONTACT_ADD);
            // allow multi-word names: phone is last token, name is everything before
            String phone = arguments.get(arguments.size() - 1);
            String name;
            if (arguments.size() == 2) {
                name = arguments.get(0);
            } else {
                List<String> nameParts = arguments.subList(0, arguments.size() - 1);
                name = String.join(" ", nameParts);
            }
            ArrayList<String> contactArgs = new ArrayList<>();
            contactArgs.add(name);
            contactArgs.add(phone);
            Contact newContact = new Contact(contactArgs);
            contactList.addContact(newContact);
            return "New contact added:\n" + String.format("%s (%s)", newContact.getName(), phone);
        case "remove":
            ArrayList<String> removeArgs = new ArrayList<>(Arrays.asList(commands));
            removeArgs.remove(0);
            removeArgs.remove(0);
            commandLengthChecker(removeArgs.size(), AlphaOne.CommandType.CONTACT_DELETE);
            String targetName = String.join(" ", removeArgs);
            Contact removed = contactList.removeContactByName(targetName);
            if (removed == null) {
                return "No contact found with that name.";
            }
            return "Contact removed:\n" + String.format("%s (%s)", removed.getName(), removed.getPhone());
        case "list":
            return contactList.getContactsString();
        default:
            throw new InvalidCommandException();
        }
    }

    // ---------- Command handlers (small, focused, private) ----------

    private String handleBye(String[] commands) throws InvalidCommandException {
        commandLengthChecker(commands.length, AlphaOne.CommandType.BYE);
        storage.save(taskList.getInternalMap());
        exit = true;
        return "Thank you for using AlphaOne! ";
    }

    private String handleList(String[] commands) throws InvalidCommandException {
        commandLengthChecker(commands.length, AlphaOne.CommandType.LIST);
        return taskList.getTasksString();
    }

    /**
     * Handles mark/unmark/delete which share index parsing and similar error handling.
     */
    private String handleMutate(String[] commands, String cmd) throws InvalidCommandException {
        AlphaOne.CommandType type = switch (cmd) {
        case "mark" -> AlphaOne.CommandType.MARK;
        case "unmark" -> AlphaOne.CommandType.UNMARK;
        default -> AlphaOne.CommandType.DELETE;
        };
        commandLengthChecker(commands.length, type);
        try {
            int taskNum = parseTaskIndex(commands[1]);
            taskList.taskExistenceChecker(taskNum);
            return switch (cmd) {
            case "mark" -> taskList.markDoneString(taskNum);
            case "unmark" -> taskList.unmarkDoneString(taskNum);
            default -> taskList.deleteTaskString(taskNum);
            };
        } catch (InvalidTaskItemException itie) {
            return itie.getMessage();
        } catch (Exception e) {
            return "Invalid task number!";
        }
    }

    private String handleFind(String[] commands) throws InvalidCommandException {
        commandLengthChecker(commands.length, AlphaOne.CommandType.FIND);
        String keyword = Parser.joinFromIndex(commands, 1);
        if (keyword.isEmpty()) {
            throw new InvalidCommandException(AlphaOne.CommandType.FIND);
        }
        return taskList.displaySearchResultsString(keyword);
    }

    private String handleTodo(String[] commands) throws IncompleteDetailsException {
        if (commands.length < 2) {
            throw new IncompleteDetailsException(AlphaOne.TaskType.TODO);
        }
        return taskList.addTaskString(todoPrep(commands), AlphaOne.TaskType.TODO);
    }

    private String handleDeadline(String[] commands)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        if (commands.length < 2) {
            throw new InvalidCommandException(AlphaOne.TaskType.DEADLINE);
        }
        ArrayList<String> tidiedDescription = Parser.descriptionPrep(commands, AlphaOne.TaskType.DEADLINE);
        return taskList.addTaskString(tidiedDescription.get(0), AlphaOne.TaskType.DEADLINE,
                tidiedDescription.get(1));
    }

    private String handleEvent(String[] commands)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        ArrayList<String> tidiedDescription = Parser.descriptionPrep(commands, AlphaOne.TaskType.EVENT);
        return taskList.addTaskString(tidiedDescription.get(0), AlphaOne.TaskType.EVENT,
                tidiedDescription.get(1), tidiedDescription.get(2));
    }

    // ---------- small helpers ----------

    private int parseTaskIndex(String token) {
        return Integer.parseInt(token);
    }

    private void commandLengthChecker(int actual, AlphaOne.CommandType type) throws InvalidCommandException {
        int expectedLength;
        switch (type) {
        case BYE, LIST, CONTACT_DELETE -> expectedLength = 1;
        case MARK, UNMARK, DELETE, FIND, CONTACT_ADD -> expectedLength = 2;
        default -> throw new InvalidCommandException();
        }
        if (expectedLength != actual) {
            throw new InvalidCommandException(type);
        }
    }

    private String todoPrep(String[] commands) {
        List<String> stringList = new ArrayList<>(Arrays.asList(commands));
        stringList.remove(0);
        return String.join(" ", stringList);
    }
}
