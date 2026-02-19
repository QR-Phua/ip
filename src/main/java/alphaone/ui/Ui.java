package alphaone.ui;

import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Simple console-based UI helper for reading input and rendering the app logo.
 *
 * <p>This class centralizes a scanner used by the application and exposes a
 * convenience method to fetch lines from standard input and the application
 * ASCII art logo. It also centralizes printing so we can adapt CLI vs GUI
 * rendering from a single place.
 */
public class Ui {
    /** A reusable border string for UI sections. */
    public static final String BORDER = "+" + "–".repeat(46) + "+";
    private static final Scanner scanner = new Scanner(System.in);

    /** Default consumer that prints each message to standard output. */
    private static final Consumer<String> STDOUT_CONSUMER = System.out::println;

    // Pluggable output consumer (GUI can register to receive messages)
    private static Consumer<String> outputConsumer = STDOUT_CONSUMER;

    // Whether the registered consumer expects raw messages (true) or already-formatted (false)
    // When true, Ui.print will send raw message (no borders); when false it will wrap with BORDER.
    private static boolean isOutputConsumerExpectingRaw = false;

    /**
     * Reads a single line from standard input.
     *
     * @return the line entered by the user.
     */
    public static String readLine() {
        return scanner.nextLine();
    }

    /**
     * Returns the ASCII-art logo used at application startup.
     *
     * @return a multi-line ASCII art string representing the AlphaOne logo.
     */
    public static String getLogo() {
        return
                """
                   _      _      _      _      _      _      _      _      _  \s
                 _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_  _( )_\s
                (_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)(_ o _)
                 (_,_)  (_,_) (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)  (_,_)
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
     * Registers an output consumer and indicates whether it expects raw messages.
     *
     * <p>If {@code expectsRaw} is true the consumer receives messages without the BORDER
     * wrapper; otherwise it receives bordered messages. Pass null to reset to the default
     * stdout consumer.
     *
     * @param consumer   the Consumer to register, or null to reset to stdout.
     * @param expectsRaw true if the consumer handles its own formatting; false for bordered output.
     */
    public static void setOutputConsumer(Consumer<String> consumer, boolean expectsRaw) {
        if (consumer == null) {
            outputConsumer = STDOUT_CONSUMER;
            isOutputConsumerExpectingRaw = false;
        } else {
            outputConsumer = consumer;
            isOutputConsumerExpectingRaw = expectsRaw;
        }
    }

    /**
     * Prints a message through the registered output consumer.
     *
     * <p>Wraps the message with standard BORDER lines unless the registered consumer
     * expects raw messages (e.g. the GUI consumer).
     *
     * @param msg the message to display (raw, without borders).
     */
    public static void print(String msg) {
        if (isOutputConsumerExpectingRaw) {
            outputConsumer.accept(msg);
        } else {
            // remove leading/trailing whitespace (spaces, tabs, newlines) to avoid extra blank lines
            String trimmed = msg == null ? "" : msg.strip();
            String out = BORDER + "\n" + trimmed + "\n" + BORDER;
            outputConsumer.accept(out);
        }
    }
}
