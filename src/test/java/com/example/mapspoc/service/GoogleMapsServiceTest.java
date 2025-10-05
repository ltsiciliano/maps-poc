package com.example.mapspoc.service;

import com.example.mapspoc.config.GoogleMapsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.http.HttpMethod.GET;

class GoogleMapsServiceTest {

    private RestTemplate restTemplate;
    private MockRestServiceServer server;
    private GoogleMapsProperties properties;
    private ObjectMapper objectMapper;
    private GoogleMapsService service;

    @BeforeEach
    void setup() {
        this.restTemplate = new RestTemplate();
        this.server = MockRestServiceServer.createServer(restTemplate);
        this.properties = new GoogleMapsProperties();
        this.properties.setApiKey("TEST_KEY");
        this.objectMapper = new ObjectMapper();
        this.service = new GoogleMapsService(restTemplate, properties, objectMapper);
    }

    @Test
    @DisplayName("geocode should call Google API and return parsed JSON")
    void geocode_ok() throws Exception {
        String expectedUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=Avenida%20Paulista&key=TEST_KEY&language=pt-BR";
        String body = "{\"results\":[{\"formatted_address\":\"Avenida Paulista\"}],\"status\":\"OK\"}";

        server.expect(ExpectedCount.once(), requestTo(new URI(expectedUrl)))
                .andExpect(method(GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        JsonNode json = service.geocode("Avenida Paulista", "pt-BR");
        assertEquals("OK", json.get("status").asText());
        assertEquals("Avenida Paulista", json.get("results").get(0).get("formatted_address").asText());
        server.verify();
    }

    @Test
    @DisplayName("reverse geocode should call Google API and return parsed JSON")
    void reverse_ok() throws Exception {
        String expectedUrl = "https://maps.googleapis.com/maps/api/geocode/json?latlng=-23.0, -46.0&key=TEST_KEY&language=en".replace(" ", "");
        String body = "{\"results\":[],\"status\":\"OK\"}";

        server.expect(ExpectedCount.once(), requestTo(new URI(expectedUrl)))
                .andExpect(method(GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        JsonNode json = service.reverseGeocode(-23.0, -46.0, "en");
        assertEquals("OK", json.get("status").asText());
        server.verify();
    }

    @Test
    @DisplayName("places autocomplete should call Google API and return parsed JSON")
    void autocomplete_ok() throws Exception {
        String expectedUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=padaria&key=TEST_KEY";
        String body = "{\"predictions\":[{\"description\":\"Padaria X\"}],\"status\":\"OK\"}";

        server.expect(ExpectedCount.once(), requestTo(new URI(expectedUrl)))
                .andExpect(method(GET))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        JsonNode json = service.placesAutocomplete("padaria", null);
        assertEquals("OK", json.get("status").asText());
        assertEquals("Padaria X", json.get("predictions").get(0).get("description").asText());
        server.verify();
    }

    @Test
    @DisplayName("should throw IllegalStateException when API key is missing")
    void missing_api_key() {
        GoogleMapsProperties badProps = new GoogleMapsProperties();
        GoogleMapsService badService = new GoogleMapsService(restTemplate, badProps, objectMapper);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> badService.geocode("Rua X", null));
        assertTrue(ex.getMessage().contains("API key"));
    }
}
