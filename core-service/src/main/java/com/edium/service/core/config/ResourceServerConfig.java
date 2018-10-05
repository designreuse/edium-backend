package com.edium.service.core.config;

import com.edium.library.config.CustomAccessTokenConverter;
import com.edium.library.config.CustomTokenConverter;
import com.edium.library.spring.AbacPermissionEvaluator;
import com.edium.library.spring.JwtAuthenticationEntryPoint;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.util.Arrays;

@Configuration
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${oauth2.resourceId}")
    private String resourceId;

    @Value("${oauth2.paths.resource_file_uri}")
    private String resourceFileUri;

    @Autowired
    private Environment environment;

    @Autowired
    private CustomAccessTokenConverter customAccessTokenConverter;

    @Autowired
    private JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public void configure(ResourceServerSecurityConfigurer securityConfigurer) {
        securityConfigurer
                .tokenServices(tokenServices())
                .resourceId(resourceId);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new CustomTokenConverter();
        jwtAccessTokenConverter.setAccessTokenConverter(customAccessTokenConverter);
        final Resource resource = new ClassPathResource(resourceFileUri);
        String publicKey;
        try {
            publicKey = IOUtils.toString(resource.getInputStream());
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
        jwtAccessTokenConverter.setVerifierKey(publicKey);

        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(env -> env.equalsIgnoreCase("test"))) {
            KeyStoreKeyFactory keyStoreKeyFactory =
                    new KeyStoreKeyFactory(new ClassPathResource(environment.getProperty("oauth2.keystore_file_uri")), environment.getProperty("oauth2.keystore_password").toCharArray());
            jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(environment.getProperty("oauth2.keystore_alias")));
        }

        return jwtAccessTokenConverter;
    }

    @Bean
    @Primary
    public ResourceServerTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    @Bean
    public DefaultTokenServices testTokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();

        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());

        return defaultTokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().disable()
                .and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                .authorizeRequests()
                .antMatchers("/api/user/checkUsernameAvailability", "/api/user/checkEmailAvailability")
                .permitAll()
                .antMatchers("/login*", "/signin/**", "/signup/**").permitAll()
                .anyRequest()
                .authenticated();
    }

    @Bean
    public PermissionEvaluator permissionEvaluator() {
        return new AbacPermissionEvaluator();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
