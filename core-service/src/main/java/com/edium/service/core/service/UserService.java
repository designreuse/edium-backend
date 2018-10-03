package com.edium.service.core.service;

import com.edium.library.model.core.User;
import com.edium.library.payload.PagedResponse;
import com.edium.service.core.payload.SignUpRequest;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getById(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    PagedResponse<User> findByOrganizationId(long organizationId, int page, int size);

    Optional<User> getByUsernameOrEmail(String usernameOrEmail);

    User register(SignUpRequest signUpRequest);

    void setCurrentOrganization(Long userId, Long organizationId);

    void setGroup(Long userId, Long groupId, List<String> roles);

}
