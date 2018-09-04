package com.edium.auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TokenBlackList {
    @Id
    private String jti;
    private String username;
    private Long expires;
    private boolean blackListed;

    public TokenBlackList() {
    }

    public TokenBlackList(String username, String jti, Long expires) {
        this.jti = jti;
        this.username = username;
        this.expires = expires;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getExpires() {
        return expires;
    }

    public void setExpires(Long expires) {
        this.expires = expires;
    }

    public boolean isBlackListed() {
        return blackListed;
    }

    public void setBlackListed(boolean blackListed) {
        this.blackListed = blackListed;
    }
}
