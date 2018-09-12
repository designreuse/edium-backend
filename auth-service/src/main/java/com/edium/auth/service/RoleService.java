package com.edium.auth.service;

import com.edium.library.model.Role;

public interface RoleService {

    Role findById(Long id);

    Role findByCode(String code);

}
