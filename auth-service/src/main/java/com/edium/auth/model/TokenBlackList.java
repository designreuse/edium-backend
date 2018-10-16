package com.edium.auth.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
public class TokenBlackList {
    @Id
    private String jti;

    @NotBlank
    private String username;

    @NotNull
    private Long expires;
    private boolean blackListed;

    public TokenBlackList() {
    }

    public TokenBlackList(String jti, @NotBlank String username, @NotNull Long expires) {
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
