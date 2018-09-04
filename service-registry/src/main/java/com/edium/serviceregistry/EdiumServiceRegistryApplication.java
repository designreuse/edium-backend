package com.edium.serviceregistry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EdiumServiceRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdiumServiceRegistryApplication.class, args);
	}
}
