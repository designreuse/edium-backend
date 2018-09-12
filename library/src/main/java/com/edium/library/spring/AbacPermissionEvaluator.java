package com.edium.library.spring;

import com.edium.library.policy.PolicyEnforcement;
import com.edium.library.policy.PolicySubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AbacPermissionEvaluator implements PermissionEvaluator {
    private static Logger logger = LoggerFactory.getLogger(AbacPermissionEvaluator.class);

    @Autowired
    PolicyEnforcement policy;

    @Override
    public boolean hasPermission(Authentication authentication , Object targetDomainObject, Object permission) {
        Map<String, Object> environment = new HashMap<>();

		/*
		Object authDetails = authentication.getDetails();
		if(authDetails != null) {
			if(authDetails instanceof WebAuthenticationDetails) {
				environment.put("remoteAddress", ((WebAuthenticationDetails) authDetails).getRemoteAddress());
			}
		}
		*/
        environment.put("time", new Date());

        PolicySubject policySubject = new PolicySubject(authentication);

        logger.debug("hasPersmission({}, {}, {})", policySubject, targetDomainObject, permission);
        return policy.check(policySubject, targetDomainObject, permission, environment);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

}
