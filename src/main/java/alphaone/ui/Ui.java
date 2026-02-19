package alphaone.ui;

import java.util.Scanner;
import java.util.function.Consumer;

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
    private static final Scanner scanner = new Scanner(System.in);

    // Pluggable output consumer (GUI can register to receive messages)
    private static Consumer<String> outputConsumer = (s) -> {
        // default prints to stdout
        System.out.println(s);
    };

    // Whether the registered consumer expects raw messages (true) or already-formatted (false)
    // When true, Ui.print will send raw message (no borders); when false it will wrap with BORDER.
    private static boolean isOutputConsumerExpectingRaw = false;

    /** Read a single line from standard input. */
    public static String readLine() {
        return scanner.nextLine();
    }

    /** Returns the ASCII-art logo used at application startup. */
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
     * Register an output consumer; the provided Consumer will receive the final
     * formatted message. Pass null to reset to default (stdout). The consumer
     * will receive formatted (bordered) messages by default.
     */
    public static void setOutputConsumer(Consumer<String> consumer) {
        setOutputConsumer(consumer, false);
    }

    /**
     * Register an output consumer and indicate whether it expects raw messages.
     * If {@code expectsRaw} is true the consumer will receive messages without
     * the BORDER wrapper; otherwise it will receive bordered messages.
     */
    public static void setOutputConsumer(Consumer<String> consumer, boolean expectsRaw) {
        if (consumer == null) {
            outputConsumer = (s) -> System.out.println(s);
            isOutputConsumerExpectingRaw = false;
        } else {
            outputConsumer = consumer;
            isOutputConsumerExpectingRaw = expectsRaw;
        }
    }

    /**
     * Centralized printing method so all classes call Ui.print(...).
     * Ui.print wraps the message with standard BORDER lines unless the registered
     * consumer expects raw messages (GUI).
     *
     * @param msg the message to display (raw, without borders)
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
