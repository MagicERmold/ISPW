package com.stocktrack.view.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class JavaFXApp extends Application {

    private static Scene scene;

    @Override
    @SuppressWarnings("java:S2696")
    public void start(Stage stage) throws IOException {
        // Carico la Scene del login
        scene = new Scene(loadFXML("login"), 800, 600);

        // Carico il css
        String css = Objects.requireNonNull(this.getClass().getResource("/com/stocktrack/view/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        // Imposto la Scene del Login
        stage.setScene(scene);
        stage.setTitle("StockTrack - Gestione Magazzino");
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.show();
    }

    // Metodo statico per cambiare schermata facilmente dai controller
    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
        // Adattiamo la finestra se necessario, o lasciamo le dimensioni fisse
    }

    // Carica il file .fxml dalla cartella resources
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFXApp.class.getResource("/com/stocktrack/view/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
}