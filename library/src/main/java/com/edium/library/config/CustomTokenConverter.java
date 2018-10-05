package com.edium.library.config;

import com.edium.library.model.UserPrincipal;
import com.edium.library.spring.Principal;
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

        UserPrincipal account = (UserPrincipal) authentication.getPrincipal();
        additionalInfo.put(Principal.ORGANIZATION_ID.toString(), account.getOrganizationId());
        additionalInfo.put(Principal.USERNAME.toString(), account.getUsername());
        additionalInfo.put(Principal.USER_ID.toString(), account.getId());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return super.enhance(accessToken, authentication);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        System.out.println("extractAuthentication map: " + map);
        OAuth2Authentication authentication = super.extractAuthentication(map);
        authentication.setDetails(map);
        return authentication;
    }

}
