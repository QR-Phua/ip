import java.util.ArrayList;
import java.util.Scanner;
public class AlphaOne {
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
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> taskList = new ArrayList<>();
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Hello! I am AlphaOne, your chatbot companion! ");
        System.out.println("Tell me what you would like to do!  ");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");

        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }  else {
                taskList.add(input);
                System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
                System.out.println("added: " + input);
                System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");

            }
        }
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
        System.out.println("Thank you for using AlphaOne! ");
        System.out.println("+––––––––––––––––––––––––––––––––––––––––––––––+");
    }
}
