package com.edium.auth.service;

import com.edium.library.model.core.Role;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.model.core.UserRole;
import com.edium.library.repository.core.UserRepository;
import com.edium.library.repository.core.UserRoleRepository;
import com.edium.library.service.core.RoleService;
import com.edium.library.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserServiceImpl implements UserService {

    /**
     * The Spring Data repository for User entities.
     */
    private final UserRepository accountRepository;

    private final UserRoleRepository userRoleRepository;

    /**
     * The Spring Data repository for Role entities
     */
    private final RoleService roleService;

    @Autowired
    public UserServiceImpl(UserRepository accountRepository, UserRoleRepository userRoleRepository, RoleService roleService) {
        this.accountRepository = accountRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleService = roleService;
    }

    /**
     * Find and return all accounts
     * @return collection of all accounts
     */
    @Override
    public Collection<User> findAll() {
        Collection<User> accounts = accountRepository.findAll();
        return accounts;
    }

    /**
     * Find core by username
     * @param username the username of the core
     * @return the core account
     */
    @Override
    public User findByUsernameOrEmail(String username) {
        return accountRepository.findByUsernameOrEmail(username, username).get();
    }

    /**
     * Create a new core as simple core. Find the simple core role from the database
     * add assign to the many to many collection
     * @param account - new User of core
     * @return - the created account
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public User createNewAccount(User account) {

        // Add the simple core role
        Role role = roleService.findByCode("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        // Validate the password
        if (account.getPassword().length() < 8){
            throw new EntityExistsException("password should be greater than 8 characters");
        }

        // Encode the password
        account.setPassword(new BCryptPasswordEncoder().encode(account.getPassword()));

        UserOrganization userOrganization = new UserOrganization();
        userOrganization.setOrganizationId(AppConstants.DEFAULT_ORGANIZATION_ID);
        userOrganization.setGroupId(AppConstants.DEFAULT_GROUP_ID);
        userOrganization.setUser(account);

        UserRole userRole = new UserRole();
        userRole.setRole(roleService.findByCode(AppConstants.DEFAULT_ROLE.toString()));
        userRole.setUserOrganization(userOrganization);

        return userRoleRepository.save(userRole).getUserOrganization().getUser();
    }

    @Override
    public User saveOrUpdate(User account) {
        return  accountRepository.save(account);
    }
}
