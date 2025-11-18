package com.example.feedback_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the Feedback API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI feedbackApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Provider Feedback Portal API")
                        .description("REST API for collecting and managing provider feedback")
                        .version("v1.0"));
    }

    @Bean
    public GroupedOpenApi feedbackApi() {
        return GroupedOpenApi.builder()
                .group("feedback")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}