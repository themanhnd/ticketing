package com.manhnt.ticketing.event.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI eventServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Event Service API")
                .description("Quản lý sân, sự kiện và khu khán đài")
                .version("v1"));
    }
}
