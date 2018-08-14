package com.edium.service.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCircuitBreaker
public class EdiumApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdiumApiGatewayApplication.class, args);
    }
}
