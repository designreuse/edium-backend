package com.edium.service.core.security;

import com.edium.service.core.model.Organization;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Map;

public class OrganizationPermission implements Permission {

    private final String ORGANIZATION_ID = "organizationId";

    @Override
    public boolean isAllowed(Authentication authentication, Object targetDomainObject, PermissionType permissionType) {
        boolean hasPermission = false;

        Long organizationId = getOrganizationId(authentication);

        if (isAuthenticated(authentication) && organizationId != null) {
            if (targetDomainObject instanceof Organization) {
                hasPermission = organizationId.equals(((Organization) targetDomainObject).getId());
            } else if (isLong(targetDomainObject)) {
                hasPermission = organizationId.equals(targetDomainObject);
            }
        }
        return hasPermission;
    }

    private Long getOrganizationId(Authentication authentication) {
        OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
        Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();

        if (details.containsKey(ORGANIZATION_ID)) {
            return Long.parseLong(details.get(ORGANIZATION_ID).toString());
        }
        return null;
    }

    private boolean isLong(Object targetDomainObject) {
        return targetDomainObject instanceof Long && (Long) targetDomainObject > 0;
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.getPrincipal() != null && !authentication.getPrincipal().toString().isEmpty();
    }
}
