package com.edium.library.config;

import com.edium.library.model.UserPrincipal;
import com.edium.library.util.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new SpringSecurityAuditAwareImpl();
    }

}

class SpringSecurityAuditAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        UserPrincipal currentUser = new UserPrincipal();
        if (authentication instanceof OAuth2Authentication) {
            currentUser = (UserPrincipal) ((OAuth2Authentication) authentication).getUserAuthentication().getPrincipal();
        }

        return Optional.ofNullable(currentUser.getId());
    }
}
