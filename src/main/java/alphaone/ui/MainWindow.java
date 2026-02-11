package alphaone.ui;

import java.util.function.Consumer;

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

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setAlphaOne(AlphaOne bot) {
        this.alphaOne = bot;
        // register Ui consumer so Ui.print(...) adds a dialog in the GUI; consumer expects raw text
        Consumer<String> guiConsumer = (s) -> Platform.runLater(() -> {
            dialogContainer.getChildren().addAll(
                    DialogBox.getDukeDialog(s, dukeImage)
            );
        });
        Ui.setOutputConsumer(guiConsumer, true);

        // Show same startup message as the CLI; tasks are already loaded by AlphaOne constructor
        String startup = alphaOne.getStartupMessage();
        if (startup != null && !startup.isEmpty()) {
            // Use Ui.print so the registered consumer will display it
            Ui.print(startup);
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage)
        );
        alphaOne.processInput(input);
        userInput.clear();

        // If the command triggered exit, close the JavaFX application window
        if (alphaOne.isExit()) {
            Platform.exit();
        }
    }
}
