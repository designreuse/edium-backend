package com.edium.service.user;

import com.netflix.appinfo.AmazonInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableCircuitBreaker
public class EdiumApiGatewayApplication {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        SpringApplication.run(EdiumApiGatewayApplication.class, args);
    }

    @Bean
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
