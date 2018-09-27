package com.edium.library.util;

import com.edium.library.exception.BadRequestException;
import com.edium.library.model.UserPrincipal;
import com.edium.library.spring.Principal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Utils {
    public static boolean isNullOrEmpty(Object value) {
        if (value instanceof String) {
            return value == null || value.toString().trim().isEmpty();
        }
        return value == null;
    }

    public static UserPrincipal getCurrentUser(Authentication authentication) {
        UserPrincipal currentUser = new UserPrincipal();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return currentUser;
        }

        if (authentication.getDetails() instanceof OAuth2AuthenticationDetails) {
            OAuth2AuthenticationDetails oauthDetails = (OAuth2AuthenticationDetails) authentication.getDetails();
            Map<String, Object> details = (Map<String, Object>) oauthDetails.getDecodedDetails();

            return getCurrentUser(details, authentication.getAuthorities());
        }

        return currentUser;
    }

    public static UserPrincipal getCurrentUser(Map<String, ?> details, Collection<? extends GrantedAuthority> authorities) {
        UserPrincipal currentUser = new UserPrincipal();

        if (details == null) {
            return currentUser;
        }

        if (!Utils.isNullOrEmpty(details.get(Principal.USER_ID.toString()))) {
            currentUser.setId(Long.parseLong(details.get(Principal.USER_ID.toString()).toString()));
        }

        if (!Utils.isNullOrEmpty(details.get(Principal.USERNAME.toString()))) {
            currentUser.setUsername(details.get(Principal.USERNAME.toString()).toString());
        }

        if (!Utils.isNullOrEmpty(details.get(Principal.ORGANIZATION_ID.toString()))) {
            currentUser.setOrganizationId(Long.parseLong(details.get(Principal.ORGANIZATION_ID.toString()).toString()));
        }

        currentUser.setRoles(new ArrayList<>());
        for (GrantedAuthority authority : authorities) {
            currentUser.getRoles().add(authority.getAuthority());
        }

        return currentUser;
    }

    public static void validatePageNumberAndSize(int page, int size) {
        if(page < 0) {
            throw new BadRequestException("Page number cannot be less than zero.");
        }

        if(size < 1) {
            throw new BadRequestException("Page size cannot be less than zero.");
        }

        if(size > AppConstants.MAX_PAGE_SIZE) {
            throw new BadRequestException("Page size must not be greater than " + AppConstants.MAX_PAGE_SIZE);
        }
    }

    public static List<Long> getGroupPathOfGroup(String rootPath, Long currGroupId) {
        List<Long> groupId = new ArrayList<>();

        String groupPath = rootPath == null ? "" : rootPath;

        String[] ids = groupPath.split("\\/");

        for (String id : ids) {
            if (id == null || id.trim().isEmpty()) {
                continue;
            }
            groupId.add(Long.parseLong(id));
        }

        if (!groupId.contains(currGroupId)) {
            groupId.add(currGroupId);
        }

        return groupId;
    }
}
