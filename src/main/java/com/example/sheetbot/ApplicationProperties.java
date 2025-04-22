package com.example.sheetbot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationProperties {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}