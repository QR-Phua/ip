package alphaone.ui;

/**
 * Simple console-based UI helper for reading input and rendering the app logo.
 *
 * This class centralizes a scanner used by the application and exposes a
 * convenience method to fetch lines from standard input and the application
 * ASCII art logo. It also centralizes printing so we can adapt CLI vs GUI
 * rendering from a single place.
 */
public class Ui {
    /** A reusable border string for UI sections. */
    public static final String BORDER = "+" + "\u2013".repeat(46) + "+";
    private static final java.util.Scanner scanner = new java.util.Scanner(System.in);

    /** Read a single line from standard input. */
    public static String readLine() {
        return scanner.nextLine();
    }

    /** Return the ASCII-art logo used at application startup. */
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
                   _     / ___ \\\\| | |_) | | | | (_| | |_| | | | |  __/     _  \s
                 _( )_  /_/   \\\\_\\_| .__/|_| |_|\\__,_|\\___/|_| |_|\\___|   _( )_\s
                (_ o _)           |_|                                   (_ o _)
                 (_,_)                                                   (_,_)
                   _      _      _      _      _      _      _      _      _  \s
                 _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
                (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
                 (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)
                """;
    }

    /**
     * Centralized printing method so all classes call Ui.print(...).
     * Ui.print wraps the message with standard BORDER lines so callers can pass
     * raw messages and rely on the UI to handle presentation.
     *
     * @param msg the message to display (raw, without borders)
     */
    public static void print(String msg) {
        System.out.println(BORDER);
        System.out.println(msg);
        System.out.println(BORDER);
    }

    /**
     * Print a message exactly as-is (no additional borders). Use for low-level
     * diagnostic messages where borders are not desired.
     */
    public static void printRaw(String msg) {
        System.out.println(msg);
    }
}
