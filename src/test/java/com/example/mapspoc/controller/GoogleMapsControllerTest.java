package com.example.mapspoc.controller;

import com.example.mapspoc.exception.GlobalExceptionHandler;
import com.example.mapspoc.service.GoogleMapsService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GoogleMapsController.class)
@Import(GlobalExceptionHandler.class)
class GoogleMapsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GoogleMapsService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/maps/geocode returns 200 with service JSON")
    void geocode_ok() throws Exception {
        JsonNode body = objectMapper.readTree("{\"status\":\"OK\"}");
        Mockito.when(service.geocode(eq("Av. Paulista"), eq("pt-BR"))).thenReturn(body);

        mockMvc.perform(get("/api/maps/geocode")
                        .param("address", "Av. Paulista")
                        .param("language", "pt-BR")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("GET /api/maps/reverse returns 200 with service JSON")
    void reverse_ok() throws Exception {
        JsonNode body = objectMapper.readTree("{\"status\":\"OK\"}");
        Mockito.when(service.reverseGeocode(eq(-23.0), eq(-46.0), isNull())).thenReturn(body);

        mockMvc.perform(get("/api/maps/reverse")
                        .param("lat", "-23.0")
                        .param("lng", "-46.0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("GET /api/maps/places/autocomplete returns 200 with service JSON")
    void autocomplete_ok() throws Exception {
        JsonNode body = objectMapper.readTree("{\"status\":\"OK\"}");
        Mockito.when(service.placesAutocomplete(eq("padaria"), isNull())).thenReturn(body);

        mockMvc.perform(get("/api/maps/places/autocomplete")
                        .param("input", "padaria")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("OK"));
    }

    @Test
    @DisplayName("Missing required parameter should return 400 with error JSON from Controller Advice")
    void missing_param_bad_request() throws Exception {
        mockMvc.perform(get("/api/maps/geocode")
                        // missing address param
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Service IllegalStateException should be mapped to 400 via Controller Advice")
    void service_error_mapped() throws Exception {
        Mockito.when(service.geocode(anyString(), any())).thenThrow(new IllegalStateException("Google Maps API key is not configured."));

        mockMvc.perform(get("/api/maps/geocode")
                        .param("address", "X")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("API key")));
    }
}
