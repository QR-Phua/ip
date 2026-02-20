package alphaone.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

/**
 * Represents a dialog box with a text bubble — no avatar images.
 * Each bubble slides in from below and fades in when added.
 */
public class DialogBox extends HBox {
    private static final Logger LOGGER = Logger.getLogger(DialogBox.class.getName());
    private static final Duration ANIM_DURATION = Duration.millis(220);
    private static final double SLIDE_FROM_Y_OFFSET = 14;

    @FXML
    private Label dialog;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load DialogBox FXML", e);
        }
        dialog.setText(text);
        playEnterAnimation();
    }

    /**
     * Plays a slide-up + fade-in entrance animation on this bubble.
     */
    private void playEnterAnimation() {
        setOpacity(0);

        TranslateTransition slide = new TranslateTransition(ANIM_DURATION, this);
        slide.setFromY(SLIDE_FROM_Y_OFFSET);
        slide.setToY(0);

        FadeTransition fade = new FadeTransition(ANIM_DURATION, this);
        fade.setFromValue(0);
        fade.setToValue(1);

        ParallelTransition enter = new ParallelTransition(slide, fade);
        enter.play();
    }

    /** Flips alignment and applies the bot reply style. */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box styled for a user message (right-aligned).
     *
     * @param text the message text to display
     * @return a new DialogBox for the user
     */
    public static DialogBox getUserDialog(String text) {
        return new DialogBox(text);
    }

    /**
     * Returns a dialog box styled for a bot reply (left-aligned, reply style).
     *
     * @param text the message text to display
     * @return a new DialogBox for the bot
     */
    public static DialogBox getBotDialog(String text) {
        DialogBox db = new DialogBox(text);
        db.flip();
        return db;
    }
}
