package com.edium.service.core.config;

import com.edium.library.model.UserPrincipal;
import com.edium.library.util.Utils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class CustomAccessTokenConverter extends DefaultAccessTokenConverter {

    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        System.out.println("convertAccessToken: " + authentication.getDetails());
        return super.convertAccessToken(token, authentication);
    }

    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        System.out.println("extractAccessToken: " + map);
        return super.extractAccessToken(value, map);
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> claims) {
        System.out.println("claims: " + claims);
        OAuth2Authentication oAuth2Authentication = super.extractAuthentication(claims);
        oAuth2Authentication.setDetails(claims);

        Authentication userAuthentication = oAuth2Authentication.getUserAuthentication();

        if (userAuthentication != null) {
            Collection<? extends GrantedAuthority> authorities = userAuthentication.getAuthorities();
            UserPrincipal currentUser = Utils.getCurrentUser(claims, authorities);

            userAuthentication = new UsernamePasswordAuthenticationToken(currentUser,
                    userAuthentication.getCredentials(), authorities);
        }
        return new OAuth2Authentication(oAuth2Authentication.getOAuth2Request(), userAuthentication);
    }
}
