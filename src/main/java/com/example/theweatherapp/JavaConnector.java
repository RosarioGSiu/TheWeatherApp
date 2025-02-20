package com.example.theweatherapp;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;

public class JavaConnector {

    private final DatePicker startDatePicker;
    private final DatePicker endDatePicker;
    private final TextArea weatherDataArea;

    public JavaConnector(DatePicker startDatePicker, DatePicker endDatePicker, TextArea weatherDataArea) {
        this.startDatePicker = startDatePicker;
        this.endDatePicker = endDatePicker;
        this.weatherDataArea = weatherDataArea;
    }

    //
    public void onMapClick(double lat, double lon) {
        System.out.println("Map clicked at: " + lat + ", " + lon);
        String url;
        if (isItaly(lat, lon)) {
            url = "https://historical-forecast-api.open-meteo.com/v1/forecast?latitude=42.5&longitude=12.5"
                    + "&start_date=" + startDatePicker.getValue()
                    + "&end_date=" + endDatePicker.getValue()
                    + "&hourly=temperature_2m"
                    + "&models=arpae_cosmo_seamless,arpae_cosmo_2i,arpae_cosmo_5m";
        } else if (isNetherlands(lat, lon)) {
            url = "https://historical-forecast-api.open-meteo.com/v1/forecast?latitude=52.52&longitude=13.41"
                    + "&start_date=" + startDatePicker.getValue()
                    + "&end_date=" + endDatePicker.getValue()
                    + "&hourly=temperature_2m"
                    + "&models=knmi_seamless,knmi_harmonie_arome_europe,knmi_harmonie_arome_netherlands";
        } else if (isMexico(lat, lon)) {
            url = "https://historical-forecast-api.open-meteo.com/v1/forecast?latitude=42.5&longitude=12.5"
                    + "&start_date=" + startDatePicker.getValue()
                    + "&end_date=" + endDatePicker.getValue()
                    + "&hourly=temperature_2m"
                    + "&models=best_match,ecmwf_ifs04,ecmwf_ifs025,ecmwf_aifs025,cma_grapes_global,"
                    + "bom_access_global,gfs_global,icon_global,gem_global,meteofrance_arpege_world,"
                    + "ukmo_global_deterministic_10km";
        } else {
            url = "https://historical-forecast-api.open-meteo.com/v1/forecast?latitude=42.5&longitude=12.5"
                    + "&start_date=" + startDatePicker.getValue()
                    + "&end_date=" + endDatePicker.getValue()
                    + "&hourly=temperature_2m"
                    + "&models=best_match";
        }
        System.out.println("Fetching weather data from: " + url);
        // Call the API and update the TextArea when data is received
        APIService.fetchWeatherData(url, response -> weatherDataArea.setText(response));
    }

  
    private boolean isItaly(double lat, double lon) {
        // Approximate Italy boundaries
        return (lat > 36 && lat < 47) && (lon > 6 && lon < 19);
    }

    private boolean isNetherlands(double lat, double lon) {
        // Approximate Netherlands boundaries
        return (lat > 50 && lat < 54) && (lon > 3 && lon < 8);
    }

    private boolean isMexico(double lat, double lon) {
        // Approximate Mexico boundaries
        return (lat > 14 && lat < 33) && (lon > -118 && lon < -86);
    }
}
