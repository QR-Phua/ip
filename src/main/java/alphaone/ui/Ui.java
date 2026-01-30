package alphaone.ui;

/**
 * Simple console-based UI helper for reading input and rendering the app logo.
 *
 * This class centralizes a scanner used by the application and exposes a
 * convenience method to fetch lines from standard input and the application
 * ASCII art logo.
 */
public class Ui {
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);

    /**
     * Read a single line from standard input.
     *
     * @return the next line from System.in (without the line terminator)
     */
    public static String readLine() {
        return scanner.nextLine();
    }

    /**
     * Return the ASCII-art logo used at application startup.
     *
     * @return the formatted logo string
     */
    public static String printLogo() {
        return
                """
                   _      _      _      _      _      _      _      _      _  \s
                 _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
                (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
                 (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)
                   _                                                       _  \s
                 _( )_      _    _       _            ___                _( )_\s
                (_ o _)    / \\  | |_ __ | |__   __ _ / _ \\ _ __   ___   (_ o _)
                 (_,_)    / _ \\ | | '_ \\| '_ \\ / _` | | | | '_ \\ / _ \\   (_,_)
                   _     / ___ \\| | |_) | | | | (_| | |_| | | | |  __/     _  \s
                 _( )_  /_/   \\_\\_| .__/|_| |_|\\__,_|\\___/|_| |_|\\___|   _( )_\s
                (_ o _)           |_|                                   (_ o _)
                 (_,_)                                                   (_,_)
                   _      _      _      _      _      _      _      _      _  \s
                 _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
                (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
                 (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)
                """;
    }
}
