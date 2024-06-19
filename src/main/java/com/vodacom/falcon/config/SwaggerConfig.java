package com.vodacom.falcon.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
@Slf4j
public class SwaggerConfig {
    @Bean
    public OpenAPI myOpenAPI() {
        Contact contact = new Contact()
                .email("faruquebraimo@gmail.com ")
                .name("Faruque Braimo");

        Info info = new Info()
                .title("Falcon-API")
                .version("1.0.0")
                .contact(contact)
                .description("Your travel assistant api");

        return new OpenAPI().info(info);
    }
}
