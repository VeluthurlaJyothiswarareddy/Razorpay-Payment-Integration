package com.razorpay.integration.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI razorpayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Razorpay Payment Integration API")
                        .description("REST APIs for Razorpay order creation, payment verification, and webhooks")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Razorpay Integration Team")
                                .email("support@example.com")));
    }
}
