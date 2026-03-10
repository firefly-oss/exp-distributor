package com.firefly.experience.distributor.web;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.web.reactive.config.EnableWebFlux;

/**
 * Spring Boot application entry point for the Experience Distributor service.
 * <p>
 * Provides REST APIs for distributor management journeys including profile,
 * branding, catalog, territories, agencies, agents, shipments, terms and
 * conditions, operations, configurations, and simulations.
 */
@SpringBootApplication(
        scanBasePackages = {
                "com.firefly.experience.distributor",
                "org.fireflyframework.web"
        }
)
@EnableWebFlux
@ConfigurationPropertiesScan
@OpenAPIDefinition(
        info = @Info(
                title = "${spring.application.name}",
                version = "${spring.application.version}",
                description = "Experience layer API for distributor management journeys",
                contact = @Contact(
                        name = "${spring.application.team.name}",
                        email = "${spring.application.team.email}"
                )
        ),
        servers = {
                @Server(
                        url = "http://core.getfirefly.io/exp-distributor",
                        description = "Development Environment"
                ),
                @Server(
                        url = "/",
                        description = "Local Development Environment"
                )
        }
)
public class ExpDistributorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpDistributorApplication.class, args);
    }
}
