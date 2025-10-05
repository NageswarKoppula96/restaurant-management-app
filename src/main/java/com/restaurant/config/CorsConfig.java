package com.restaurant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Bean
    @Profile("!test") // Don't register this bean in test profile
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Handle allowed origins
        if (allowedOrigins != null && allowedOrigins.length > 0 && !allowedOrigins[0].isEmpty()) {
            // Check if we have any wildcard patterns
            boolean hasWildcard = Arrays.stream(allowedOrigins).anyMatch(origin -> origin.contains("*"));
            
            if (hasWildcard) {
                // If we have wildcard patterns, use allowedOriginPatterns
                config.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
                // When using patterns with credentials, we need to be explicit
                if (allowCredentials) {
                    config.setAllowCredentials(true);
                }
            } else {
                // No wildcards, use regular allowedOrigins
                config.setAllowedOrigins(Arrays.asList(allowedOrigins));
                config.setAllowCredentials(allowCredentials);
            }
        } else {
            // Default to allowing all origins using patterns
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            // When using patterns with credentials, we need to be explicit
            if (allowCredentials) {
                config.setAllowedOrigins(null); // Clear any previously set origins
            }
        }
        
        // Set allowed methods
        Arrays.stream(allowedMethods)
              .flatMap(method -> Arrays.stream(method.split(",")))
              .map(String::trim)
              .filter(method -> !method.isEmpty())
              .forEach(config::addAllowedMethod);
        
        // Set allowed headers
        if (allowedHeaders != null && allowedHeaders.length > 0 && !allowedHeaders[0].isEmpty()) {
            Arrays.stream(allowedHeaders)
                  .flatMap(header -> Arrays.stream(header.split(",")))
                  .map(String::trim)
                  .filter(header -> !header.isEmpty())
                  .forEach(config::addAllowedHeader);
        }
        
        // Configure credentials
        config.setAllowCredentials(allowCredentials);
        
        // Set max age
        config.setMaxAge(maxAge);
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
