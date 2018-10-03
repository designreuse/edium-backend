package com.edium.service.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"com.edium.library.repository", "com.edium.service.core.repository"})
@EntityScan(basePackageClasses = {
        Jsr310JpaConverters.class
}, basePackages = {"com.edium.library.model", "com.edium.service.core.model"})
public class UnitTestConfig {
}
