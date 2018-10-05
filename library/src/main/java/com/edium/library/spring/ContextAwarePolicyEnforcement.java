package com.edium.library.spring;

import com.edium.library.policy.PolicyEnforcement;
import com.edium.library.policy.PolicySubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class ContextAwarePolicyEnforcement {
    @Autowired
    protected PolicyEnforcement policy;

    public void checkPermission(Object resource, String permission, String resourceType) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> environment = new HashMap<>();

		/*
		Object authDetails = auth.getDetails();
		if(authDetails != null) {
			if(authDetails instanceof WebAuthenticationDetails) {
				environment.put("remoteAddress", ((WebAuthenticationDetails) authDetails).getRemoteAddress());
			}
		}
		*/
        environment.put("time", new Date());

        PolicySubject policySubject = new PolicySubject(auth);

        if(!policy.check(policySubject, resource, permission + "_" + resourceType, environment))
            throw new AccessDeniedException("Access is denied");
    }

    public void checkPermission(Object resource, PermissionType permission, PermissionObject resourceType) {
        checkPermission(resource, permission.toString(), resourceType.toString());
    }
}
