package com.edium.library.policy;

import com.edium.library.spring.Principal;
import com.edium.library.util.Utils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PolicySubject {
    private Long userId;
    private String username;
    private Long organizationId;
    private List<String> roles;

    public PolicySubject() {
    }

    public PolicySubject(Authentication authentication) {
        if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
            Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();

            if (!Utils.isNullOrEmpty(details.get(Principal.USER_ID.toString()))) {
                this.userId = Long.parseLong(details.get(Principal.USER_ID.toString()).toString());
            }

            if (!Utils.isNullOrEmpty(details.get(Principal.USERNAME.toString()))) {
                this.username = details.get(Principal.USERNAME.toString()).toString();
            }

            if (!Utils.isNullOrEmpty(details.get(Principal.ORGANIZATION_ID.toString()))) {
                this.organizationId = Long.parseLong(details.get(Principal.ORGANIZATION_ID.toString()).toString());
            }

            this.roles = new ArrayList<>();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                this.roles.add(authority.getAuthority());
            }
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
