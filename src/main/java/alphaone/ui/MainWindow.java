package alphaone.ui;

import alphaone.AlphaOne;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private AlphaOne alphaOne;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));

    private Image dukeImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    /**
     * Initializes the controller after its root element has been completely processed.
     *
     * <p>Verifies that all FXML-injected fields are not null and binds the scroll pane's
     * vertical value to the dialog container height so new dialogs are scrolled into view.
     */
    @FXML
    public void initialize() {
        assert(this.userImage != null);
        assert(this.dukeImage != null);
        assert(this.userInput != null);
        assert(this.sendButton != null);
        assert(this.scrollPane != null);
        assert(this.dialogContainer != null);
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setAlphaOne(AlphaOne bot) {
        this.alphaOne = bot;
        String startup = alphaOne.getStartupMessage();
        assert(startup != null);
        if (startup != null && !startup.isEmpty()) {
            dialogContainer.getChildren().addAll(
                    DialogBox.getDukeDialog(startup, dukeImage)
            );
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = alphaOne.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();

        // If the command triggered exit, close the JavaFX application window
        if (alphaOne.isExit()) {
            Platform.exit();
        }
    }
}
