package com.example.mapspoc.controller;

import com.example.mapspoc.service.GoogleMapsService;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/maps", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@Tag(name = "Google Maps Integration", description = "Endpoints to consume Google Maps APIs (Geocoding, Reverse Geocoding, Places Autocomplete)")
@RequiredArgsConstructor
public class GoogleMapsController {

    private final GoogleMapsService service;

    @GetMapping("/geocode")
    @Operation(summary = "Geocode an address", description = "Returns Google Geocoding API response for a given address")
    public ResponseEntity<JsonNode> geocode(
            @RequestParam @NotBlank @Parameter(description = "Textual address to geocode") String address,
            @RequestParam(required = false) @Parameter(description = "Optional BCP-47 language code (e.g., pt-BR, en)") String language
    ) {
        return ResponseEntity.ok(service.geocode(address, language));
    }

    @GetMapping("/reverse")
    @Operation(summary = "Reverse geocode coordinates", description = "Returns Google Geocoding API response for provided lat/lng")
    public ResponseEntity<JsonNode> reverseGeocode(
            @RequestParam @NotNull @Parameter(description = "Latitude") Double lat,
            @RequestParam @NotNull @Parameter(description = "Longitude") Double lng,
            @RequestParam(required = false) @Parameter(description = "Optional BCP-47 language code (e.g., pt-BR, en)") String language
    ) {
        return ResponseEntity.ok(service.reverseGeocode(lat, lng, language));
    }

    @GetMapping("/places/autocomplete")
    @Operation(summary = "Places Autocomplete", description = "Returns Google Places Autocomplete predictions for an input text")
    public ResponseEntity<JsonNode> placesAutocomplete(
            @RequestParam @NotBlank @Parameter(description = "User input string for place autocomplete") String input,
            @RequestParam(required = false) @Parameter(description = "Optional BCP-47 language code (e.g., pt-BR, en)") String language
    ) {
        return ResponseEntity.ok(service.placesAutocomplete(input, language));
    }
}
