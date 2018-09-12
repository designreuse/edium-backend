package com.edium.auth.security;

import com.edium.auth.service.TokenBlackListService;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

public class CustomTokenService extends DefaultTokenServices {
    Logger logger = LoggerFactory.getLogger(CustomTokenService.class);

    @Autowired
    private TokenBlackListService blackListService;

    @Autowired
    private TokenStore tokenStore;

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken token = super.createAccessToken(authentication);

        System.out.println("token expired: " + this.getAccessTokenValiditySeconds(authentication.getOAuth2Request()));

        addToEnabledList(token, authentication, false);

        return token;
    }

    private void addToEnabledList(OAuth2AccessToken token, OAuth2Authentication authentication, boolean check) {
        String jti = (String) token.getAdditionalInformation().get("jti");

        logger.info("addToEnabledList: " + jti);

        if (!check || !blackListService.existJti(jti)) {
            String username = "";
            if (authentication.getPrincipal() instanceof UserPrincipal) {
                username = ((UserPrincipal) authentication.getPrincipal()).getUsername();
            } else {
                username = authentication.getPrincipal().toString();
            }

            blackListService.addToEnabledList(username, jti, token.getExpiration().getTime());
        }
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
        logger.info("refresh token:" + refreshTokenValue);
        String jti = tokenRequest.getRequestParameters().get("jti");
        try {
            if (jti != null && blackListService.isBlackListed(jti)) {
                return null;
            }

            OAuth2AccessToken token = super.refreshAccessToken(refreshTokenValue, tokenRequest);

            addToEnabledList(token, this.tokenStore.readAuthenticationForRefreshToken(this.tokenStore.readRefreshToken(refreshTokenValue)), true);

            blackListService.addToBlackList(jti);
            return token;
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public void revokeAllToken(String username) {
        try {
            blackListService.addAllToBlackList(username);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
