package com.edium.service.data.config;

import com.edium.service.data.service.CourseService;
import com.edium.service.data.service.CourseServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UnitTestConfig {

    @Bean
    public CourseService courseService() {
        return Mockito.mock(CourseServiceImpl.class);
    }

}
