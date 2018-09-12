package com.edium.auth.service;

import com.edium.library.model.User;

import java.util.Collection;

public interface UserService {

    Collection<User> findAll();

    User findByUsernameOrEmail(String userename);

    User createNewAccount(User account);

    User saveOrUpdate(User account);
}
