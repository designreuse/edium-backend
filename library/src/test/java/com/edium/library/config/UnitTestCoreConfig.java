package com.edium.library.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"com.edium.library.repository.core"})
@EntityScan(basePackageClasses = {
        Jsr310JpaConverters.class
}, basePackages = {"com.edium.library.model.core", "com.edium.library.model.base"})
public class UnitTestCoreConfig {
}
