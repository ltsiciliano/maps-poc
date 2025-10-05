package com.example.mapspoc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        // Buffering allows us to read the response body multiple times if needed (e.g., for logging)
        RestTemplate rt = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        // Add any interceptors (e.g., logging) here in the future
        rt.setInterceptors(List.of());
        return rt;
    }
}
