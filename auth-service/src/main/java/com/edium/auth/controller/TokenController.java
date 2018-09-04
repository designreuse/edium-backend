package com.edium.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
public class TokenController extends BaseController {

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private TokenStore tokenStore;

    @RequestMapping(method = RequestMethod.POST, value = "tokens/revokeTokenById/{tokenId}")
    @ResponseBody
    public void revokeToken(HttpServletRequest request, @PathVariable String tokenId) {
        tokenServices.revokeToken(tokenId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "tokens/{clientId}/{username}")
    @ResponseBody
    public List<String> getTokens(@PathVariable String clientId, @PathVariable(required = false) String username) {
        List<String> tokenValues = new ArrayList<>();

        Collection<OAuth2AccessToken> tokens;
        if (username == null || username.trim().isEmpty()) {
            tokens = tokenStore.findTokensByClientId(clientId);
        } else {
            tokens = tokenStore.findTokensByClientIdAndUserName(clientId, username);
        }

        if (tokens != null) {
            for (OAuth2AccessToken token : tokens) {
                tokenValues.add(token.getValue());
            }
        }
        return tokenValues;
    }

    @RequestMapping(method = RequestMethod.POST, value = "tokens/revokeRefreshToken/{tokenId:.*}")
    @ResponseBody
    public String revokeRefreshToken(@PathVariable String tokenId) {
        if (tokenStore instanceof JdbcTokenStore) {
            ((JdbcTokenStore) tokenStore).removeRefreshToken(tokenId);
        }
        return tokenId;
    }

}
