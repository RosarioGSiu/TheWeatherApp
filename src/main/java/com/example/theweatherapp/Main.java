package com.example.theweatherapp;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URL;
import java.time.LocalDate;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create the root layout
        BorderPane root = new BorderPane();

        // Create top pane with DatePickers for start and end dates
        HBox topPane = new HBox(10);
        DatePicker startDatePicker = new DatePicker(LocalDate.of(2025, 2, 5));
        DatePicker endDatePicker = new DatePicker(LocalDate.of(2025, 2, 18));
        topPane.getChildren().addAll(new Label("Start Date:"), startDatePicker,
                new Label("End Date:"), endDatePicker);

        // Create WebView for the interactive map
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Create a TextArea to display the weather data
        TextArea weatherTextArea = new TextArea();
        weatherTextArea.setEditable(false);
        weatherTextArea.setPrefHeight(200);

        // Create JavaConnector instance that handles map clicks and UI updates
        JavaConnector connector = new JavaConnector(startDatePicker, endDatePicker, weatherTextArea);

        // Load the local map.html file from resources
        URL mapUrl = getClass().getResource("/map.html");
        if (mapUrl != null) {
            webEngine.load(mapUrl.toExternalForm());
        } else {
            System.out.println("map.html not found in resources!");
        }

        // After the page loads, expose the JavaConnector to JavaScript
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", connector);
            }
        });

        // Assemble the scene
        root.setTop(topPane);
        root.setCenter(webView);
        root.setBottom(weatherTextArea);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Weather Map App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
