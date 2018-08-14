package com.edium.api.gateway;

import com.edium.api.gateway.filters.AuthHeaderFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableZuulProxy
@EnableCircuitBreaker
public class EdiumApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EdiumApiGatewayApplication.class, args);
    }

    @Bean
    AuthHeaderFilter authHeaderFilter() {
        return new AuthHeaderFilter();
    }
}
