package com.edium.service.data.config;

import com.edium.service.data.service.CourseService;
import com.edium.service.data.service.CourseServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories({"com.edium.library.repository.share", "com.edium.service.data.repository"})
@EntityScan(basePackageClasses = {
        Jsr310JpaConverters.class
}, basePackages = {"com.edium.library.model.share", "com.edium.service.data.model"})
public class UnitTestConfig {

    @Bean
    public CourseService courseService() {
        return Mockito.mock(CourseServiceImpl.class);
    }

}
