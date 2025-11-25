package com.example.feedback_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Development-only CORS configuration to allow external GraphQL UIs to send requests to
 * the application's /graphql endpoint. Remove or lock down for production.
 */
@Configuration
public class GraphQLCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/graphql")
        // Allow localhost dev frontends. Restrict in production.
        .allowedOrigins("http://localhost:3000")
                .allowedMethods("POST", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
