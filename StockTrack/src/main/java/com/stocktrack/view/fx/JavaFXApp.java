package com.stocktrack.view.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFXApp extends Application {

    private static Scene scene;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        scene = new Scene(loadFXML("login"), 800, 600); // Ho aumentato un po' la dimensione

        String css = this.getClass().getResource("/com/stocktrack/view/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setScene(scene);
        stage.setTitle("StockTrack - Gestione Magazzino");
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