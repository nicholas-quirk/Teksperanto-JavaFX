package teksperanto.javafx;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author Nicholas Quirk
 */
public class WordPad extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        new UI(stage);
    }
}
