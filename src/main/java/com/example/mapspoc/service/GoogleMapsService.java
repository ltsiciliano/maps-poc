package com.example.mapspoc.service;

import com.example.mapspoc.config.GoogleMapsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    private static final String BASE_URL = "https://maps.googleapis.com/maps/api";

    private final RestTemplate restTemplate;
    private final GoogleMapsProperties properties;
    private final ObjectMapper objectMapper;

    public JsonNode geocode(String address, String language) {
        ensureApiKey();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("address", address);
        params.add("key", properties.getApiKey());
        if (language != null && !language.isBlank()) {
            params.add("language", language);
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/geocode/json")
                .queryParams(params)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return toJsonNode(response.getBody());
    }

    public JsonNode reverseGeocode(double lat, double lng, String language) {
        ensureApiKey();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("latlng", lat + "," + lng);
        params.add("key", properties.getApiKey());
        if (language != null && !language.isBlank()) {
            params.add("language", language);
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/geocode/json")
                .queryParams(params)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return toJsonNode(response.getBody());
    }

    public JsonNode placesAutocomplete(String input, String language) {
        ensureApiKey();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("input", input);
        params.add("key", properties.getApiKey());
        if (language != null && !language.isBlank()) {
            params.add("language", language);
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/place/autocomplete/json")
                .queryParams(params)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUri();
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        return toJsonNode(response.getBody());
    }

    private void ensureApiKey() {
        if (properties == null || properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new IllegalStateException("Google Maps API key is not configured. Set google.maps.api-key or env GOOGLE_MAPS_API_KEY.");
        }
    }

    private JsonNode toJsonNode(String body) {
        try {
            return objectMapper.readTree(Objects.requireNonNullElse(body, "{}"));
        } catch (Exception ex) {
            throw new RuntimeException("Failed to parse response from Google Maps API", ex);
        }
    }
}
