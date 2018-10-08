package com.edium.library.service;

import com.edium.library.exception.ResourceExistException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.Role;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.RoleRepository;
import com.edium.library.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByCode(String roleCode) {
        return roleRepository.findByCode(roleCode).orElseThrow(() -> new ResourceNotFoundException("Role", "code", roleCode));
    }

    @Override
    public Role findById(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));
    }

    @Override
    public PagedResponse<Role> findAll(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, new Sort(Sort.Direction.ASC, "code"));

        Page<Role> roles = roleRepository.findAll(pageable);

        return new PagedResponse<>(roles.getContent(), roles.getNumber(),
                roles.getSize(), roles.getTotalElements(), roles.getTotalPages(), roles.isLast());
    }

    @Override
    public Role save(Role role) {
        if (role.getId() == null) {
            try {
                findByCode(role.getCode());
                throw new ResourceExistException("Role", "code", role.getCode());
            } catch (ResourceNotFoundException ex) {
                return roleRepository.save(role);
            }
        } else {
            try {
                Role existRole = findByCode(role.getCode());

                if (!existRole.getId().equals(role.getId())) {
                    throw new ResourceExistException("Role", "code", role.getCode());
                } else {
                    return roleRepository.save(role);
                }
            } catch (ResourceNotFoundException ex) {
                return roleRepository.save(role);
            }
        }
    }

    @Override
    public void deleteByCode(String roleCode) {
        roleRepository.delete(findByCode(roleCode));
    }

    @Override
    public void deleteById(Long roleId) {
        roleRepository.delete(findById(roleId));
    }

    @Override
    public void flush() {
        roleRepository.flush();
    }
}
