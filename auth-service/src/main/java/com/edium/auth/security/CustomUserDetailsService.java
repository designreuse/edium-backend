package com.edium.auth.security;

import com.edium.library.model.core.User;
import com.edium.library.model.UserPrincipal;
import com.edium.library.repository.core.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        // Let people login with either username or email
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail)
                );

        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            // No Roles assigned to core...
            throw new UsernameNotFoundException("User not authorized.");
        }

        return UserPrincipal.create(user);
    }
}