package alphaone.ui;

import java.util.function.Consumer;

import alphaone.core.AlphaOne;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    // Delay before bot response appears — feels natural, like the bot is thinking
    private static final Duration BOT_RESPONSE_DELAY = Duration.millis(600);
    // Threshold for vvalue considered "at bottom" — used to re-enable auto-scroll
    private static final double SCROLL_AT_BOTTOM_THRESHOLD = 0.99;
    // Scroll speed multiplier applied to trackpad delta
    private static final double SCROLL_SPEED_MULTIPLIER = 1.5;
    // Duration of the animated scroll-to-bottom transition
    private static final Duration SCROLL_ANIMATION_DURATION = Duration.millis(250);

    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;

    private AlphaOne alphaOne;

    // Track whether to auto-scroll on new user-side content
    private boolean autoScroll = true;
    private Timeline scrollTimeline;

    /**
     * Initialises the scroll pane with gesture scrolling and auto-scroll behaviour.
     */
    @FXML
    private void initialize() {
        // Smooth animated auto-scroll when content height grows
        dialogContainer.heightProperty().addListener((obs, oldH, newH) -> {
            if (autoScroll) {
                smoothScrollToBottom();
            }
        });

        // Re-enable auto-scroll when user manually scrolls back to the bottom
        scrollPane.vvalueProperty().addListener((obs, oldV, newV) ->
                autoScroll = newV.doubleValue() >= SCROLL_AT_BOTTOM_THRESHOLD);

        // Trackpad / mouse-wheel gesture scrolling
        scrollPane.setOnScroll(e -> {
            double viewportH = scrollPane.getViewportBounds().getHeight();
            double contentH = dialogContainer.getBoundsInLocal().getHeight();
            if (contentH <= viewportH) {
                return;
            }
            double shift = (e.getDeltaY() / contentH) * -SCROLL_SPEED_MULTIPLIER;
            double next = Math.min(1.0, Math.max(0.0, scrollPane.getVvalue() + shift));
            scrollPane.setVvalue(next);
            e.consume();
        });
    }

    /**
     * Smoothly animates the scroll pane to the bottom using {@link #SCROLL_ANIMATION_DURATION}.
     */
    private void smoothScrollToBottom() {
        if (scrollTimeline != null) {
            scrollTimeline.stop();
        }
        scrollTimeline = new Timeline(
            new KeyFrame(SCROLL_ANIMATION_DURATION,
                new KeyValue(scrollPane.vvalueProperty(), 1.0))
        );
        scrollTimeline.play();
    }

    /**
     * Injects the AlphaOne instance and shows a clean GUI greeting (no ASCII logo).
     */
    public void setAlphaOne(AlphaOne bot) {
        this.alphaOne = bot;
        Ui.setOutputConsumer(createGuiConsumer(), true);
        Ui.print(bot.getGuiGreeting());
    }

    /**
     * Handles user input: adds user bubble, processes command, clears field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        autoScroll = true;
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        // Explicitly scroll so the user always sees their sent message
        smoothScrollToBottom();
        alphaOne.handleInput(input);
        userInput.clear();

        if (alphaOne.isExit()) {
            Platform.exit();
        }
    }

    /**
     * Creates the output consumer that appends bot responses after a short delay,
     * then always scrolls the view to the newest message.
     */
    private Consumer<String> createGuiConsumer() {
        return message -> Platform.runLater(() -> {
            PauseTransition delay = new PauseTransition(BOT_RESPONSE_DELAY);
            delay.setOnFinished(event -> {
                // Always scroll to the bot reply — user should never miss a response
                autoScroll = true;
                dialogContainer.getChildren().add(DialogBox.getBotDialog(message));
                smoothScrollToBottom();
            });
            delay.play();
        });
    }
}
