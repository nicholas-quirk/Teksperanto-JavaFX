package teksperanto.javafx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Nicholas Quirk
 */
public class UI {

    // GUI members.
    TextArea textArea;
    MenuBar menuBar;
    Menu fileMenu;
    TextField searchField;
    Button searchButton;
    TextArea searchResults;
    FileChooser fc;
    // Helper members.
    HashMap<String, String> eoToEn = new HashMap();
    ArrayList<String> results = new ArrayList();
    Long lastKeyPressTime = 0l;

    public UI(final Stage stage) {

        // Initialize components.
        createFileMenu(stage);
        createFileMenuChoices(stage);
        createTextArea();
        createSearchField();
        createDictionarySearchButton();
        createSearchResults();

        // Set layout.
        BorderPane border = new BorderPane();

        HBox hBoxMenu = new HBox();
        hBoxMenu.getChildren().add(menuBar);
        border.setTop(hBoxMenu);

        BorderPane centerBorder = new BorderPane();

        HBox hBoxSearch = new HBox();
        hBoxSearch.getChildren().addAll(searchField, searchButton);

        centerBorder.setTop(hBoxSearch);
        centerBorder.setCenter(textArea);
        centerBorder.setBottom(searchResults);

        border.setCenter(centerBorder);

        Scene scene = new Scene(border, 400, 600);

        stage.setTitle("Teksperanto-JavaFX");
        stage.setScene(scene);
        stage.show();

        EsperantoSubstitutor es = new EsperantoSubstitutor(textArea, lastKeyPressTime);

        es.start();

        eoToEn = (new EspdicLoader()).createDictionary();
    }

    private void createSearchResults() {
        searchResults = new TextArea();
    }

    private void createSearchField() {
        searchField = new TextField();
    }

    private void createDictionarySearchButton() {
        searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchText = EsperantoSubstitutor
                        .translateCharacters(searchField.getText());
                boolean exactMatch = (searchText.startsWith("'") && searchText
                        .endsWith("'"));
                for (String key : eoToEn.keySet()) {
                    if (exactMatch) {
                        if (key.trim().equalsIgnoreCase(
                                searchText.replace("'", ""))) {
                            results.add(key + " : " + eoToEn.get(key));
                        }
                        if (eoToEn.get(key).trim()
                                .equalsIgnoreCase(searchText.replace("'", ""))) {
                            results.add(key + " : " + eoToEn.get(key));
                        }
                    } else {
                        if (key.contains(searchText)) {
                            results.add(key + " : " + eoToEn.get(key));
                        }
                        if (eoToEn.get(key).contains(searchText)) {
                            results.add(key + " : " + eoToEn.get(key));
                        }
                    }
                }
                String sr = "";
                for (String s : results) {
                    sr += s + "\n";
                }
                searchResults.setText(sr);
                results = new ArrayList<String>();
            }
        });
    }

    private void createFileMenu(final Stage stage) {
        fileMenu = new Menu("File");

        menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(stage.widthProperty());
        menuBar.getMenus().add(fileMenu);
    }

    private void createFileMenuChoices(final Stage stage) {
        MenuItem menuItemExit = new MenuItem();
        menuItemExit.setText("Exit");
        menuItemExit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        ExtensionFilter filter = new ExtensionFilter("Text file", "txt");

        fc = new FileChooser();
        fc.getExtensionFilters().add(filter);

        MenuItem menuItemOpen = new MenuItem();
        menuItemOpen.setText("Open");
        menuItemOpen.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File f = fc.showOpenDialog(stage);

                if (f != null) {
                    textArea.setText(readFile(f));
                }
            }
        });

        MenuItem menuItemSave = new MenuItem();
        menuItemSave.setText("Save");
        menuItemSave.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                File f = fc.showSaveDialog(stage);

                if (f != null) {
                    writeFile(f, textArea.getText());
                }
            }
        });

        fileMenu.getItems().addAll(menuItemOpen, menuItemSave, menuItemExit);
    }

    private void createTextArea() {
        textArea = new TextArea("Saluton!");
        textArea.setWrapText(true);
        textArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                lastKeyPressTime = System.currentTimeMillis();
            }
        });
    }

    private String readFile(File file) {
        String everything = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            try {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                everything = sb.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return everything;
    }

    private void writeFile(File file, String content) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
