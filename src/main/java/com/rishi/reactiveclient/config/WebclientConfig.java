package com.rishi.reactiveclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebclientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder().baseUrl(WebClientProperties.BASE_URL).build();
    }
}