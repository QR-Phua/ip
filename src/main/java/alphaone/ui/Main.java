package alphaone.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import alphaone.core.AlphaOne;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX Application entry point for AlphaOne.
 */
public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final double DEFAULT_FONT_SIZE = 14;
    private static final double MIN_WINDOW_HEIGHT = 480;
    private static final double MIN_WINDOW_WIDTH = 520;

    private final AlphaOne alphaOne = new AlphaOne();

    @Override
    public void start(Stage stage) {
        // Load bundled SF Pro font so it works identically on all OSes and inside the JAR
        Font.loadFont(Main.class.getResourceAsStream("/fonts/SF-Pro.ttf"), DEFAULT_FONT_SIZE);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();

            // Warm peach → soft lavender gradient background
            ap.setBackground(new Background(new BackgroundFill(
                new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0.00, Color.web("#FFF6E6")),
                    new Stop(0.20, Color.web("#FFE4C8")),
                    new Stop(0.40, Color.web("#FFD0B8")),
                    new Stop(0.60, Color.web("#F9C0C0")),
                    new Stop(0.80, Color.web("#E8BCDA")),
                    new Stop(1.00, Color.web("#D4B8F0"))
                ),
                CornerRadii.EMPTY, Insets.EMPTY
            )));

            Scene scene = new Scene(ap);
            stage.setScene(scene);
            stage.setTitle("AlphaOne");
            stage.setMinHeight(MIN_WINDOW_HEIGHT);
            stage.setMinWidth(MIN_WINDOW_WIDTH);
            fxmlLoader.<MainWindow>getController().setAlphaOne(alphaOne);
            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load MainWindow FXML", e);
        }
    }
}

