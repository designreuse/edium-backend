package com.edium.auth.service;

import com.edium.library.model.core.User;
import com.edium.library.model.core.Role;
import com.edium.library.repository.core.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class UserServiceBean implements UserService {

    /**
     * The Logger for this class.
     */
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The Spring Data repository for User entities.
     */
    @Autowired
    private UserRepository accountRepository;

    /**
     * The Spring Data repository for Role entities
     */
    @Autowired
    private RoleService roleService;

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
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
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

        // Create the role
        account.setRoles(roles);
        return accountRepository.save(account);
    }

    @Override
    public User saveOrUpdate(User account) {
        return  accountRepository.save(account);
    }
}
