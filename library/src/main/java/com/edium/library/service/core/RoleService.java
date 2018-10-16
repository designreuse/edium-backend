package com.edium.library.service.core;

import com.edium.library.model.core.Role;
import com.edium.library.payload.PagedResponse;

public interface RoleService {

    Role findByCode(String roleCode);

    Role findById(Long roleId);

    PagedResponse<Role> findAll(int page, int size);

    Role save(Role role);

    void deleteByCode(String roleCode);

    void deleteById(Long roleId);

    void flush();

}
