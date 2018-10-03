package com.edium.service.core.service;

import com.edium.library.exception.AppException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.Role;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.model.core.UserRole;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.RoleRepository;
import com.edium.library.repository.core.UserOrganizationRepository;
import com.edium.library.repository.core.UserRepository;
import com.edium.library.repository.core.UserRoleRepository;
import com.edium.library.util.AppConstants;
import com.edium.library.util.Utils;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.payload.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserRoleRepository userRoleRepository;

    @Autowired
    UserOrganizationRepository userOrganizationRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    OrganizationService organizationService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public PagedResponse<User> findByOrganizationId(long organizationId, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "name");
        Page<User> users = userRepository.findByOrganizationId(organizationId, pageable);

        return new PagedResponse<>(users.getContent(), users.getNumber(),
                users.getSize(), users.getTotalElements(), users.getTotalPages(), users .isLast());
    }

    @Override
    public Optional<User> getByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class, noRollbackFor = AppException.class)
    public User register(SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AppException("Username is already taken!");
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AppException("Email Address already in use!");
        }

        // Creating user's account
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserOrganization userOrganization = new UserOrganization();
        userOrganization.setOrganizationId(AppConstants.DEFAULT_ORGANIZATION_ID);
        userOrganization.setGroupId(AppConstants.DEFAULT_GROUP_ID);
        userOrganization.setUser(user);

        UserRole userRole = new UserRole();
        userRole.setRole(roleRepository.findByCode(AppConstants.DEFAULT_ROLE.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "code", AppConstants.DEFAULT_ROLE.toString())));
        userRole.setUserOrganization(userOrganization);

        return userRoleRepository.save(userRole).getUserOrganization().getUser();
    }

    @Override
    public void setCurrentOrganization(Long userId, Long organizationId) {
        Organization organization = organizationService.findById(organizationId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.setOrganizationId(organization.getId());
        user.setGroupId(groupService.getGroupOfUserInOrganization(userId, organizationId));

        userRepository.save(user);
    }

    @Override
    @Transactional(noRollbackFor = {ResourceNotFoundException.class, AppException.class}, rollbackFor = RuntimeException.class)
    public void setGroup(Long userId, Long groupId, List<String> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Group group = groupService.findById(groupId);

        // Check role list
        if (roles == null || roles.isEmpty()) {
            throw new AppException("Role list is empty");
        }
        List<Role> roleList = roleRepository.findAll();
        Map<String, Role> mapRole = new HashMap<>();
        roleList.forEach(role -> mapRole.put(role.getCode(), role));

        roles.forEach(role -> {
            if (!mapRole.containsKey(role)) {
                throw new ResourceNotFoundException("Role", "code", role);
            }
        });

        UserOrganization userOrganization = userOrganizationRepository.getByUser_IdAndOrganizationId(user.getId(), group.getOrganization().getId());

        if (userOrganization == null) {
            // User don't in Organization
            UserOrganization userOrganization1 = new UserOrganization();
            userOrganization1.setGroupId(groupId);
            userOrganization1.setUser(user);
            userOrganization1.setOrganizationId(group.getOrganization().getId());

            List<UserRole> userRoleList = new ArrayList<>();
            roles.forEach(role -> userRoleList.add(new UserRole(userOrganization1, mapRole.get(role))));

            userRoleRepository.saveAll(userRoleList);
        } else {
            // User in Organization
            if (!group.getId().equals(userOrganization.getGroupId())) {
                // Group change
                userOrganization.setGroupId(group.getId());

                userOrganizationRepository.save(userOrganization);
            }

            // List roles in DB
            List<UserRole> oldRoleList = userRoleRepository.getByUserOrganization_Id(userOrganization.getId());

            // List new roles to set
            List<UserRole> newRoleList = new ArrayList<>();
            roles.forEach(role -> newRoleList.add(new UserRole(userOrganization, mapRole.get(role))));

            // Find role to delete
            oldRoleList.forEach(userRole -> {
                if (!roles.contains(userRole.getRole().getCode())) {
                    userRoleRepository.delete(userRole);
                }
            });

            // Find role to insert
            newRoleList.forEach(userRole -> {
                boolean flag = false;
                for (UserRole userRole1 : oldRoleList) {
                    if (userRole1.getRole().getCode().equals(userRole.getRole().getCode())) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    userRoleRepository.save(userRole);
                }
            });
        }
    }

}
