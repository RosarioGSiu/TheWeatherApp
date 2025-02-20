package com.example.theweatherapp;

import javafx.application.Platform;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class APIService {

    public static void fetchWeatherData(String url, Consumer<String> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Platform.runLater(() -> callback.accept(response));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> callback.accept("Primary API call failed: " + e.getMessage() + "\nTrying fallback API..."));
                    fetchFromFallback(callback);
                    return null;
                });
    }

    private static void fetchFromFallback(Consumer<String> callback) {
        Platform.runLater(() -> callback.accept("Fallback API call not implemented yet."));
    }
}
