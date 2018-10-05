package com.edium.library.spring;

import com.edium.library.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.Collections;
import java.util.HashMap;

@Component
public class OAuthHelper {

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private Environment environment;

    public RequestPostProcessor bearerToken(UserPrincipal userPrincipal) {
        return mockRequest -> {
            OAuth2AccessToken token = createAccessToken(userPrincipal);
            mockRequest.addHeader("Authorization", "Bearer " + token.getValue());
            return mockRequest;
        };
    }

    OAuth2AccessToken createAccessToken(UserPrincipal userPrincipal) {
        OAuth2Request oAuth2Request = new OAuth2Request(new HashMap<>(), "client_test", null,
                true, null, Collections.singleton(environment.getProperty("oauth2.resourceId")), null, Collections.emptySet(), Collections.emptyMap());

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        OAuth2Authentication auth = new OAuth2Authentication(oAuth2Request, authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(auth);

        return tokenServices.createAccessToken(auth);
    }

}
