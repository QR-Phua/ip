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
    private Image botImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the AlphaOne instance and registers the GUI output consumer. */
    public void setAlphaOne(AlphaOne bot) {
        this.alphaOne = bot;
        Ui.setOutputConsumer(createGuiConsumer(), true);

        String startup = alphaOne.getStartupMessage();
        if (startup != null && !startup.isEmpty()) {
            Ui.print(startup);
        }
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing the bot's reply,
     * then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage)
        );
        alphaOne.handleInput(input);
        userInput.clear();

        if (alphaOne.isExit()) {
            Platform.exit();
        }
    }

    /** Creates the output consumer that appends bot responses to the dialog container. */
    private Consumer<String> createGuiConsumer() {
        return message -> Platform.runLater(() ->
                dialogContainer.getChildren().addAll(DialogBox.getBotDialog(message, botImage)));
    }
}
