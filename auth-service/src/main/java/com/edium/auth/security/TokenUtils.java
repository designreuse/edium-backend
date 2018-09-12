package com.edium.auth.security;

import com.edium.library.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class TokenUtils {

    @Autowired
    private DefaultOAuth2RequestFactory defaultOAuth2RequestFactory;

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    PasswordEncoder passwordEncoder;

    public OAuth2AccessToken getToken(UserPrincipal principal, String clientId) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        parameters.put("grant_type", "password");
        parameters.put("password", principal.getUsername());
        parameters.put("username", principal.getPassword());

        AuthorizationRequest authorizationRequest = defaultOAuth2RequestFactory.createAuthorizationRequest(parameters);
        authorizationRequest.setApproved(true);

        OAuth2Request oauth2Request = defaultOAuth2RequestFactory.createOAuth2Request(authorizationRequest);

        // Create principal and auth token
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(principal, null,
                principal.getAuthorities());

        OAuth2Authentication authenticationRequest = new OAuth2Authentication(oauth2Request, loginToken);
        authenticationRequest.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(loginToken);

        OAuth2AccessToken accessToken = tokenServices.createAccessToken(authenticationRequest);

        return accessToken;
    }
}
