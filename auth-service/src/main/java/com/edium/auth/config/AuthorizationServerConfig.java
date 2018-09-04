package com.edium.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${oauth2.paths.token}")
    private String tokenPath;

    @Value("${oauth2.paths.check_token}")
    private String checkTokenPath;

    @Value("${oauth2.paths.token_key}")
    private String tokenKeyPath;

    @Value("${oauth2.paths.authorize}")
    private String authorizePath;

    @Value("${oauth2.paths.confirm_access}")
    private String confirmAccessPath;

    @Value("${oauth2.keystore_file_uri}")
    private String keystoreFileUri;

    @Value("${oauth2.keystore_password}")
    private String keystorePassword;

    @Value("${oauth2.keystore_alias}")
    private String keystoreAlias;

    @Value("${oauth2.access_token_validity_seconds}")
    private int accessTokenValiditySeconds;

    @Value("${oauth2.refresh_token_validity_seconds}")
    private int refreshTokenValiditySeconds;

    @Value("${oauth2.reuse_refresh_token}")
    private boolean reuseRefreshToken;

    @Autowired
    private DataSource datasource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    CustomUserDetailsService userDetailsService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();

        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter()));

        endpoints
                .tokenStore(tokenStore())
                .tokenEnhancer(tokenEnhancerChain)
                .tokenServices(tokenServices())
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .accessTokenConverter(accessTokenConverter())
                .approvalStoreDisabled()
                .pathMapping("/oauth/token", tokenPath)
                .pathMapping("/oauth/check_token", checkTokenPath)
                .pathMapping("/oauth/token_key", tokenKeyPath)
                .pathMapping("/oauth/authorize", authorizePath)
                .pathMapping("/oauth/confirm_access", confirmAccessPath);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
        //return new JdbcTokenStore(datasource);
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new CustomTokenConverter();
        KeyStoreKeyFactory keyStoreKeyFactory =
                new KeyStoreKeyFactory(new ClassPathResource(keystoreFileUri), keystorePassword.toCharArray());
        jwtAccessTokenConverter.setKeyPair(keyStoreKeyFactory.getKeyPair(keystoreAlias));
        return jwtAccessTokenConverter;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        CustomTokenService defaultTokenServices = new CustomTokenService();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setTokenEnhancer(accessTokenConverter());
        defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
        defaultTokenServices.setReuseRefreshToken(reuseRefreshToken);
        return defaultTokenServices;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                .jdbc(datasource)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security
                .checkTokenAccess("isAuthenticated()")
                .passwordEncoder(passwordEncoder);
    }

}
