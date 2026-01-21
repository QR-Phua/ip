import java.util.ArrayList;
import java.util.Scanner;
public class AlphaOne {
    private static Scanner scanner = new Scanner(System.in);
    private static ArrayList<Task> taskList = new ArrayList<>();
    public static void main(String[] args) {
        String logo =
        """
           _      _      _      _      _      _      _      _      _  \s
         _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
        (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
         (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)\s
           _                                                       _  \s
         _( )_      _    _       _            ___                _( )_\s
        (_ o _)    / \\  | |_ __ | |__   __ _ / _ \\ _ __   ___   (_ o _)
         (_,_)    / _ \\ | | '_ \\| '_ \\ / _` | | | | '_ \\ / _ \\   (_,_)\s
           _     / ___ \\| | |_) | | | | (_| | |_| | | | |  __/     _  \s
         _( )_  /_/   \\_\\_| .__/|_| |_|\\__,_|\\___/|_| |_|\\___|   _( )_\s
        (_ o _)           |_|                                   (_ o _)
         (_,_)                                                   (_,_)\s
           _      _      _      _      _      _      _      _      _  \s
         _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
        (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
         (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)\s
        """;
        System.out.println(logo);


        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Hello! I am AlphaOne, your chatbot companion!");
        System.out.println("Tell me what you would like to do!");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("bye")) {
                break;
            } else if (input.equalsIgnoreCase("list")) {
                getTasks();
            } else {
                Task newTask = new Task(input);
                taskList.add(newTask);
                System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                System.out.println("added: " + input);
                System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");

            }
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Thank you for using AlphaOne! ");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }

    private static void getTasks() {
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        if (!taskList.isEmpty()) {
            System.out.printf("You have these tasks in your list:%n");
            for (int i = 0; i < taskList.size(); i++) {
                Task currentTask = taskList.get(i);
                System.out.printf("%d. [%s] %s%n", i + 1, currentTask.getStatus(), currentTask.getDescription() );
            }
        } else {
            System.out.println("Your task list is currently empty!");
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }
}
