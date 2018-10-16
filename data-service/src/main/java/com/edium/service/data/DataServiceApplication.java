package com.edium.service.data;

import com.netflix.appinfo.AmazonInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
@EnableCircuitBreaker
@EnableResourceServer
@EnableWebSecurity
@EntityScan(basePackageClasses = {
        DataServiceApplication.class,
        Jsr310JpaConverters.class
}, basePackages = {"com.edium.library.model.share", "com.edium.service.data.model"})
@ComponentScan(basePackages = {"com.edium.library", "com.edium.service.data"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "com.edium.library.service.core.*"))
@EnableJpaRepositories({"com.edium.library.repository.share", "com.edium.service.data.repository"})
public class DataServiceApplication {

    @Value("${server.port}")
    private int port;

    @PostConstruct
    void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(DataServiceApplication.class, args);
    }

    @Bean
    @Profile({"dev", "prod"})
    public EurekaInstanceConfigBean eurekaInstanceConfig(InetUtils inetUtils) {
        System.out.println("hehe");
        EurekaInstanceConfigBean config = new EurekaInstanceConfigBean(inetUtils);
        AmazonInfo info = AmazonInfo.Builder.newBuilder().autoBuild("eureka");

        if (info != null) {
            config.setDataCenterInfo(info);
            info.getMetadata()
                    .put(AmazonInfo.MetaDataKey.publicHostname.getName(), info.get(AmazonInfo.MetaDataKey.publicHostname));
            config.setHostname(info.get(AmazonInfo.MetaDataKey.publicHostname));
            config.setIpAddress(info.get(AmazonInfo.MetaDataKey.publicIpv4));
            config.setNonSecurePort(port);
        } else {
            System.out.println("AmazonInfo is null");
        }

        return config;
    }
}
