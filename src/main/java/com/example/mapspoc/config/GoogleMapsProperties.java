package com.example.mapspoc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "google.maps")
public class GoogleMapsProperties {
    /**
     * Google Maps API Key. Configure via env var GOOGLE_MAPS_API_KEY or application.yml
     */
    private String apiKey;
}
