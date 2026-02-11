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
import alphaone.ui.Ui;

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
     * Process a textual user input. This method will print responses via Ui.
     *
     * @param input user input line
     */
    public void process(String input) {
        String[] commands = Parser.splitInput(input);
        String cmd = commands.length > 0 ? commands[0].toLowerCase() : "";
        try {
            switch (cmd) {
            case "contact" -> handleContact(commands);
            case "bye" -> handleBye(commands);
            case "list" -> handleList(commands);
            case "mark", "unmark", "delete" -> handleMutate(commands, cmd);
            case "find" -> handleFind(commands);
            case "todo" -> handleTodo(commands);
            case "deadline" -> handleDeadline(commands);
            case "event" -> handleEvent(commands);
            default -> throw new InvalidCommandException();
            }
        } catch (InvalidCommandException | IncompleteDetailsException | InvalidDateTimeException exe) {
            Ui.print(exe.getMessage());
        }
    }

    private void handleContact(String[] commands) throws InvalidCommandException {
        if (commands.length < 2) {
            throw new InvalidCommandException();
        }
        String action = commands[1].toLowerCase();
        switch (action) {
        case "add":
            ArrayList<String> arguments = new ArrayList<>(Arrays.asList(commands));
            arguments.remove(0);
            arguments.remove(0);
            commandLengthChecker(arguments.size(), AlphaOne.CommandType.CONTACT_ADD);
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
            Ui.print("New contact added:\n" + String.format("%s (%s)", newContact.getName(), phone));
            return;
        case "remove":
            ArrayList<String> removeArgs = new ArrayList<>(Arrays.asList(commands));
            removeArgs.remove(0);
            removeArgs.remove(0);
            commandLengthChecker(removeArgs.size(), AlphaOne.CommandType.CONTACT_DELETE);
            String targetName = String.join(" ", removeArgs);
            Contact removed = contactList.removeContactByName(targetName);
            if (removed == null) {
                Ui.print("No contact found with that name.");
                return;
            }
            Ui.print("Contact removed:\n" + String.format("%s (%s)", removed.getName(), removed.getPhone()));
            return;
        case "list":
            Ui.print(contactList.getContactsString());
            return;
        default:
            throw new InvalidCommandException();
        }
    }

    // ---------- Command handlers (small, focused, private) ----------

    private void handleBye(String[] commands) throws InvalidCommandException {
        commandLengthChecker(commands.length, AlphaOne.CommandType.BYE);
        storage.save(taskList.getInternalMap());
        exit = true;
        Ui.print("Thank you for using AlphaOne! ");
    }

    private void handleList(String[] commands) throws InvalidCommandException {
        commandLengthChecker(commands.length, AlphaOne.CommandType.LIST);
        taskList.getTasks();
    }

    /**
     * Handles mark/unmark/delete which share index parsing and similar error handling.
     */
    private void handleMutate(String[] commands, String cmd) throws InvalidCommandException {
        AlphaOne.CommandType type = switch (cmd) {
        case "mark" -> AlphaOne.CommandType.MARK;
        case "unmark" -> AlphaOne.CommandType.UNMARK;
        default -> AlphaOne.CommandType.DELETE;
        };
        commandLengthChecker(commands.length, type);
        try {
            int taskNum = parseTaskIndex(commands[1]);
            taskList.taskExistenceChecker(taskNum);
            switch (cmd) {
            case "mark" -> Ui.print(taskList.markDoneString(taskNum));
            case "unmark" -> Ui.print(taskList.unmarkDoneString(taskNum));
            default -> Ui.print(taskList.deleteTaskString(taskNum));
            }
        } catch (InvalidTaskItemException itie) {
            Ui.print(itie.getMessage());
        } catch (Exception e) {
            Ui.print("Invalid task number!");
        }
    }

    private void handleFind(String[] commands) throws InvalidCommandException {
        commandLengthChecker(commands.length, AlphaOne.CommandType.FIND);
        String keyword = Parser.joinFromIndex(commands, 1);
        if (keyword.isEmpty()) {
            throw new InvalidCommandException(AlphaOne.CommandType.FIND);
        }
        Ui.print(taskList.displaySearchResultsString(keyword));
    }

    private void handleTodo(String[] commands) throws IncompleteDetailsException {
        if (commands.length < 2) {
            throw new IncompleteDetailsException(AlphaOne.TaskType.TODO);
        }
        Ui.print(taskList.addTaskString(todoPrep(commands), AlphaOne.TaskType.TODO));
    }

    private void handleDeadline(String[] commands)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        if (commands.length < 2) {
            throw new InvalidCommandException(AlphaOne.TaskType.DEADLINE);
        }
        ArrayList<String> tidiedDescription = Parser.descriptionPrep(commands, AlphaOne.TaskType.DEADLINE);
        Ui.print(taskList.addTaskString(tidiedDescription.get(0), AlphaOne.TaskType.DEADLINE,
                tidiedDescription.get(1)));
    }

    private void handleEvent(String[] commands)
            throws InvalidCommandException, IncompleteDetailsException, InvalidDateTimeException {
        ArrayList<String> tidiedDescription = Parser.descriptionPrep(commands, AlphaOne.TaskType.EVENT);
        Ui.print(taskList.addTaskString(tidiedDescription.get(0), AlphaOne.TaskType.EVENT,
                tidiedDescription.get(1), tidiedDescription.get(2)));
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
