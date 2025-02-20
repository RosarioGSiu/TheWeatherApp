package com.example.theweatherapp;

import javafx.application.Platform;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

public class APIService {

    // Primary API call to Open-Meteo using dynamic lat and lon
    public static void fetchWeatherData(String url, double lat, double lon, Consumer<String> callback) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Platform.runLater(() -> callback.accept("Open-Meteo data:\n" + response));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> callback.accept("Open-Meteo call failed: " + e.getMessage() + "\nTrying fallback APIs...\n"));
                    fetchFromFallback(lat, lon, callback);
                    return null;
                });
    }

    // Fallback logic: try OpenWeather, then StormGlass.ai, then WeatherBit using dynamic lat and lon
    private static void fetchFromFallback(double lat, double lon, Consumer<String> callback) {
        HttpClient client = HttpClient.newHttpClient();

        // Fallback 1: OpenWeather
        String openWeatherApiKey = "YOUR_OPENWEATHER_API_KEY";
        String openWeatherUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat
                + "&lon=" + lon + "&appid=" + openWeatherApiKey;
        HttpRequest requestOpenWeather = HttpRequest.newBuilder()
                .uri(URI.create(openWeatherUrl))
                .build();
        client.sendAsync(requestOpenWeather, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> {
                    Platform.runLater(() -> callback.accept("OpenWeather data:\n" + response));
                })
                .exceptionally(e -> {
                    // Fallback 2: StormGlass.ai
                    String stormGlassApiKey = "YOUR_STORMGLASS_API_KEY";
                    String stormGlassUrl = "https://api.stormglass.io/v2/weather/point?lat=" + lat
                            + "&lng=" + lon + "&params=airTemperature&key=" + stormGlassApiKey;
                    HttpRequest requestStormGlass = HttpRequest.newBuilder()
                            .uri(URI.create(stormGlassUrl))
                            .build();
                    client.sendAsync(requestStormGlass, HttpResponse.BodyHandlers.ofString())
                            .thenApply(HttpResponse::body)
                            .thenAccept(response -> {
                                Platform.runLater(() -> callback.accept("StormGlass data:\n" + response));
                            })
                            .exceptionally(e2 -> {
                                // Fallback 3: WeatherBit
                                String weatherBitApiKey = "YOUR_WEATHERBIT_API_KEY";
                                String weatherBitUrl = "https://api.weatherbit.io/v2.0/current?lat=" + lat
                                        + "&lon=" + lon + "&key=" + weatherBitApiKey;
                                HttpRequest requestWeatherBit = HttpRequest.newBuilder()
                                        .uri(URI.create(weatherBitUrl))
                                        .build();
                                client.sendAsync(requestWeatherBit, HttpResponse.BodyHandlers.ofString())
                                        .thenApply(HttpResponse::body)
                                        .thenAccept(response -> {
                                            Platform.runLater(() -> callback.accept("WeatherBit data:\n" + response));
                                        })
                                        .exceptionally(e3 -> {
                                            Platform.runLater(() -> callback.accept("All fallback APIs failed: " + e3.getMessage()));
                                            return null;
                                        });
                                return null;
                            });
                    return null;
                });
    }
}
