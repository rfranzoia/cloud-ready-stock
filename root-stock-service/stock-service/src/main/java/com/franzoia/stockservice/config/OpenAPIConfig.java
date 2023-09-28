package com.franzoia.stockservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

@Configuration
public class OpenAPIConfig {

  @Bean
  public OpenAPI myOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl("http://localhost:8081/stock-service");
    devServer.setDescription("Server URL in Development environment");

    Contact contact = new Contact();
    contact.setEmail("rfranzoia@gmail.com");
    contact.setName("Romeu Franzoia");
    contact.setUrl("https://www.github.com/rfranzoia");

    License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

    Info info = new Info()
        .title("Stock Management API")
        .version("1.0")
        .contact(contact)
        .description("This API exposes endpoints to manage stock.")
        .license(mitLicense);

    return new OpenAPI().info(info).servers(List.of(devServer));
  }
}
