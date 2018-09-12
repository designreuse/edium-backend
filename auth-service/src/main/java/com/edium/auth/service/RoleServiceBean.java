package com.edium.auth.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.Role;
import com.edium.library.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Manage the data from database from Role table core
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleServiceBean implements RoleService{


    /**
     * The Spring Data repository for User entities.
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * Get by id
     * @param id
     * @return
     */
    @Override
    public Role findById(Long id) {
        Role role = roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return role;
    }

    /**
     * File Role by code
     * @param code - the code of the role
     * @return Role object
     */
    @Override
    public Role findByCode(String code) {
        return roleRepository.findByCode(code).get();
    }
}
