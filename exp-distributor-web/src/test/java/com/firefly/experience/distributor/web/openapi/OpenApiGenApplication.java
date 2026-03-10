package com.firefly.experience.distributor.web.openapi;

import org.fireflyframework.web.openapi.EnableOpenApiGen;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Minimal Spring Boot application used only during the Maven build to expose
 * the OpenAPI spec via Springdoc.
 */
@EnableOpenApiGen
@ComponentScan(basePackages = "com.firefly.experience.distributor.web.controllers")
public class OpenApiGenApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenApiGenApplication.class, args);
    }
}
