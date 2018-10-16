package com.edium.service.data.config;

import com.edium.service.data.service.CourseService;
import com.edium.service.data.service.CourseServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;

public class UnitTestConfig {

    @Bean
    public CourseService courseService() {
        return Mockito.mock(CourseServiceImpl.class);
    }

}
