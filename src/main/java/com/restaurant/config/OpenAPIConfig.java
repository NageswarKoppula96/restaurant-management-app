package com.restaurant.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI restaurantManagementAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Restaurant Management API")
                        .description("API for managing restaurant operations including menu, orders, and customers")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Restaurant Support")
                                .email("support@restaurant.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
