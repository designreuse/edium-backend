package com.edium.service.core.service;

import com.edium.library.model.core.User;
import com.edium.service.core.model.Group;

import java.util.Optional;

public interface UserService {

    Optional<User> getById(Long userId);

    Group getGroupOfUser(Long userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
