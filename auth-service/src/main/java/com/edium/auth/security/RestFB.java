package com.edium.auth.security;

import com.edium.library.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RestFB {

    @Value("${facebook.appId}")
    private String appId;

    @Value("${facebook.appSecret}")
    private String appSecret;

    @Value("${facebook.token_link}")
    private String tokenLink;

    public String getToken(final String code) throws IOException {
        String link = String.format(tokenLink, appId, appSecret, "https://localhost:8081/signin", code);
        String response = Request.Get(link).execute().returnContent().asString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response).get("access_token");
        return node.textValue();
    }

    public com.restfb.types.User getUserInfo(final String accessToken) {
        FacebookClient facebookClient = new DefaultFacebookClient(accessToken, appSecret,
                Version.LATEST);
        return facebookClient.fetchObject("me", com.restfb.types.User.class,
                Parameter.with("fields", "email,first_name,last_name,gender,cover,name"));
    }

    public void buildUser(com.restfb.types.User user, User localUser) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        localUser.setUsername(user.getEmail());
        localUser.setEnabled(true);
        localUser.setEmail(user.getEmail());
        localUser.setName(user.getName());
        localUser.setProvider("FACEBOOK");
    }
}
