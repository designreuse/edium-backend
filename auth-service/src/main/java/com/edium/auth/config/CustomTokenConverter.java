package com.edium.auth.config;

import com.edium.auth.model.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenConverter extends JwtAccessTokenConverter {

    @Value("${oauth2.custom_token_field_name}")
    private String customTokenFieldName;

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        final Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put(customTokenFieldName, authentication.getName());
        System.out.println("authentication.getPrincipal(): " + authentication.getPrincipal());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return super.enhance(accessToken, authentication);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        OAuth2Authentication authentication = super.extractAuthentication(map);
        authentication.setDetails(map);
        return authentication;
    }

}
