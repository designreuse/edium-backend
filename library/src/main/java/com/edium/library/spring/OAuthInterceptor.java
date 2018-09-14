package com.edium.library.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class OAuthInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private AuthenticationHolder holder;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (holder.getToken() == null) {
            //throw new IOException("Token not set");
            System.out.println("##################### Token not set! ###################");
        } else {
            System.out.println("##################### Token found: " + holder.getToken());
            HttpHeaders headers = request.getHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + holder.getToken());
        }

        return execution.execute(request, body);
    }
}
