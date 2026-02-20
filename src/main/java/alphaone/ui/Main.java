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

    // Gradient stop positions
    private static final double GRADIENT_STOP_0 = 0.00;
    private static final double GRADIENT_STOP_1 = 0.20;
    private static final double GRADIENT_STOP_2 = 0.40;
    private static final double GRADIENT_STOP_3 = 0.60;
    private static final double GRADIENT_STOP_4 = 0.80;
    private static final double GRADIENT_STOP_5 = 1.00;

    // Gradient colours (warm peach to soft lavender)
    private static final String GRADIENT_COLOR_0 = "#FFF6E6";
    private static final String GRADIENT_COLOR_1 = "#FFE4C8";
    private static final String GRADIENT_COLOR_2 = "#FFD0B8";
    private static final String GRADIENT_COLOR_3 = "#F9C0C0";
    private static final String GRADIENT_COLOR_4 = "#E8BCDA";
    private static final String GRADIENT_COLOR_5 = "#D4B8F0";

    private final AlphaOne alphaOne = new AlphaOne();

    @Override
    public void start(Stage stage) {
        // Load bundled SF Pro font so it works identically on all OSes and inside the JAR
        Font.loadFont(Main.class.getResourceAsStream("/fonts/SF-Pro.ttf"), DEFAULT_FONT_SIZE);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            ap.setBackground(buildGradientBackground());
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

    /**
     * Builds the warm peach to soft lavender gradient background applied to the main window.
     *
     * @return the constructed Background instance.
     */
    private static Background buildGradientBackground() {
        return new Background(new BackgroundFill(
            new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(GRADIENT_STOP_0, Color.web(GRADIENT_COLOR_0)),
                new Stop(GRADIENT_STOP_1, Color.web(GRADIENT_COLOR_1)),
                new Stop(GRADIENT_STOP_2, Color.web(GRADIENT_COLOR_2)),
                new Stop(GRADIENT_STOP_3, Color.web(GRADIENT_COLOR_3)),
                new Stop(GRADIENT_STOP_4, Color.web(GRADIENT_COLOR_4)),
                new Stop(GRADIENT_STOP_5, Color.web(GRADIENT_COLOR_5))
            ),
            CornerRadii.EMPTY, Insets.EMPTY
        ));
    }
}

