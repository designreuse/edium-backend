package com.edium.service.core.security;

import com.edium.library.spring.PermissionObject;
import com.edium.library.spring.PermissionType;
import com.edium.service.core.exception.PermissionNotDefinedException;
import com.google.common.base.Enums;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import javax.transaction.Transactional;
import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    @Transactional
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        boolean hasPermission = false;

        if (permission != null && permission.toString().contains("_")) {
            String[] arr = permission.toString().split("_");

            PermissionType permissionType = Enums.getIfPresent(PermissionType.class, arr[0]).get();
            String permissionObj = permission.toString().substring(permissionType.toString().length() + 1);

            if (canHandle(authentication, targetDomainObject, permissionObj, permissionType)) {
                hasPermission = checkPermission(authentication, targetDomainObject, permissionObj, permissionType);
            }
        }
        return hasPermission;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        boolean hasPermission = false;
        if (canHandle(authentication, targetId, targetType, permission)) {
            hasPermission = checkPermission(authentication, targetId, targetType, (PermissionType) permission);
        }
        return hasPermission;
    }

    private boolean canHandle(Authentication authentication, Object targetDomainObject, Object permissionObj, Object permissionType) {
        return targetDomainObject != null && authentication != null && permissionObj != null && permissionType != null;
    }

    private boolean checkPermission(Authentication authentication, Object targetDomainObject, String permissionObj, PermissionType permissionType) {
        verifyPermissionIsDefined(permissionObj);
        Permission permission = null;

        switch (Enums.getIfPresent(PermissionObject.class, permissionObj).get()) {
            case ORGANIZATION:
                permission = new OrganizationPermission();
                break;
        }

        return permission.isAllowed(authentication, targetDomainObject, permissionType);
    }

    private void verifyPermissionIsDefined(Object permissionKey) {
        if (!Enums.getIfPresent(PermissionObject.class, permissionKey.toString()).isPresent()) {
            throw new PermissionNotDefinedException("No permission with key " + permissionKey + " is defined in " + this.getClass().toString());
        }
    }
}
