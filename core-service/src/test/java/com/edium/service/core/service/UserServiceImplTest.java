package com.edium.service.core.service;

import com.edium.library.exception.AppException;
import com.edium.library.exception.BadRequestException;
import com.edium.library.exception.ResourceExistException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.Role;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.model.core.UserRole;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.UserOrganizationRepository;
import com.edium.library.repository.core.UserRepository;
import com.edium.library.repository.core.UserRoleRepository;
import com.edium.library.service.core.RoleService;
import com.edium.library.util.AppConstants;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.payload.SignUpRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class UserServiceImplTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);

    private final UserRoleRepository userRoleRepository = Mockito.mock(UserRoleRepository.class);

    private final UserOrganizationRepository userOrganizationRepository = Mockito.mock(UserOrganizationRepository.class);

    private final GroupService groupService = Mockito.mock(GroupService.class);

    private final OrganizationService organizationService = Mockito.mock(OrganizationService.class);

    private final RoleService roleService = Mockito.mock(RoleService.class);

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final UserService userService = new UserServiceImpl(userRepository, userRoleRepository,
            userOrganizationRepository, groupService, organizationService, roleService, passwordEncoder);

    @Test
    public void whenGetById_thenReturn() {
        // setup
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        user.setId(1L);

        // when
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // then
        Optional<User> response = userService.getById(user.getId());
        Assert.assertTrue(response.isPresent());
        Assert.assertEquals(response.get(), user);
    }

    @Test
    public void whenGetById_thenReturnEmpty() {
        // setup
        Long userId = 1L;

        // when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // then
        Assert.assertFalse(userService.getById(userId).isPresent());
    }

    @Test
    public void whenExistsByUsername_withExist_thenReturnTrue() {
        // setup
        String username = "test";

        // when
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);

        // then
        Assert.assertTrue(userService.existsByUsername(username));
    }

    @Test
    public void whenExistsByUsername_withNoExist_thenReturnFalse() {
        // setup
        String username = "test";

        // when
        Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);

        // then
        Assert.assertFalse(userService.existsByUsername(username));
    }

    @Test
    public void whenExistsByEmail_withExist_thenReturnTrue() {
        // setup
        String username = "test";

        // when
        Mockito.when(userRepository.existsByEmail(username)).thenReturn(true);

        // then
        Assert.assertTrue(userService.existsByEmail(username));
    }

    @Test
    public void whenExistsByEmail_withNoExist_thenReturnFalse() {
        // setup
        String username = "test";

        // when
        Mockito.when(userRepository.existsByEmail(username)).thenReturn(false);

        // then
        Assert.assertFalse(userService.existsByEmail(username));
    }

    @Test(expected = BadRequestException.class)
    public void whenFindByOrganizationId_withPageNegative_thenException() {
        try {
            int page = -1, size = 10;

            userService.findByOrganizationId(1L, page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page number cannot be less than zero.");
            throw ex;
        }
    }

    @Test(expected = BadRequestException.class)
    public void whenFindByOrganizationId_withSizeNegative_thenException() {
        try {
            int page = 0, size = -1;

            userService.findByOrganizationId(1L, page, size);
        } catch (BadRequestException ex) {
            Assert.assertEquals(ex.getMessage(), "Page size cannot be less than zero.");
            throw ex;
        }
    }

    @Test
    public void whenFindByOrganizationId_thenOk() {
        // setup
        int page = 0, size = 10;
        Long organizationId = 1L;
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "name");
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");

        // when
        Mockito.when(userRepository.findByOrganizationId(organizationId, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(user)));

        // then
        PagedResponse<User> userPagedResponse = userService.findByOrganizationId(organizationId, page, size);
        Assert.assertEquals(userPagedResponse.getTotalElements(), 1);
        Assert.assertEquals(userPagedResponse.getContent().get(0), user);
    }

    @Test
    public void whenGetByUsernameOrEmail_thenReturnEmpty() {
        // setup
        String usernameOrEmail = "test";

        // when
        Mockito.when(userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)).thenReturn(Optional.empty());

        // then
        Assert.assertFalse(userService.getByUsernameOrEmail(usernameOrEmail).isPresent());
    }

    @Test
    public void whenGetByUsernameOrEmail_thenReturn() {
        // setup
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");
        String usernameOrEmail = user.getUsername();

        // when
        Mockito.when(userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)).thenReturn(Optional.of(user));

        // then
        Optional<User> optionalUser = userService.getByUsernameOrEmail(usernameOrEmail);
        Assert.assertTrue(optionalUser.isPresent());
        Assert.assertEquals(optionalUser.get(), user);
    }

    @Test(expected = ResourceExistException.class)
    public void whenRegister_withUsernameExist_thenException() {
        try {
            // setup
            SignUpRequest signUpRequest = new SignUpRequest("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(),
                    "test@gmail.com", "12345678");

            // when
            Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(true);

            // then
            userService.register(signUpRequest);
        } catch (ResourceExistException ex) {
            Assert.assertEquals(ex.getResourceName(), "User");
            Assert.assertEquals(ex.getFieldName(), "username");
            throw ex;
        }
    }

    @Test(expected = ResourceExistException.class)
    public void whenRegister_withEmailExist_thenException() {
        try {
            // setup
            SignUpRequest signUpRequest = new SignUpRequest("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(),
                    "test@gmail.com", "12345678");

            // when
            Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
            Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(true);

            // then
            userService.register(signUpRequest);
        } catch (ResourceExistException ex) {
            Assert.assertEquals(ex.getResourceName(), "User");
            Assert.assertEquals(ex.getFieldName(), "email");
            throw ex;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenRegister_withRoleDefaultNotFound_thenException() {
        try {
            // setup
            SignUpRequest signUpRequest = new SignUpRequest("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(),
                    "test@gmail.com", "12345678");

            // when
            Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
            Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
            Mockito.when(roleService.findByCode(AppConstants.DEFAULT_ROLE.toString())).then(invocationOnMock -> {
               throw new ResourceNotFoundException("Role", "code", AppConstants.DEFAULT_ROLE.toString());
            });

            // then
            userService.register(signUpRequest);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Role");
            Assert.assertEquals(ex.getFieldName(), "code");
            throw ex;
        }
    }

    @Test
    public void whenRegister_thenOk() {
        // setup
        SignUpRequest signUpRequest = new SignUpRequest("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis(),
                "test@gmail.com", "12345678");

        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        UserRole userRole = new UserRole();
        userRole.setUserOrganization(new UserOrganization());
        userRole.getUserOrganization().setUser(user);

        // when
        Mockito.when(userRepository.existsByUsername(signUpRequest.getUsername())).thenReturn(false);
        Mockito.when(userRepository.existsByEmail(signUpRequest.getEmail())).thenReturn(false);
        Mockito.when(roleService.findByCode(AppConstants.DEFAULT_ROLE.toString())).thenReturn(new Role(AppConstants.DEFAULT_ROLE.toString(), AppConstants.DEFAULT_ROLE.toString()));
        Mockito.when(userRoleRepository.save(Mockito.any())).thenReturn(userRole);

        // then
        User response = userService.register(signUpRequest);
        Assert.assertEquals(response.getPassword(), user.getPassword());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetCurrentOrganization_withOrganizationNotFound_thenException() {
        try {
            // setup
            Long userId = 1L, organizationId = 1L;

            // when
            Mockito.when(organizationService.findById(organizationId)).then(invocationOnMock -> {
                throw new ResourceNotFoundException("Organization", "id", organizationId);
            });

            // then
            userService.setCurrentOrganization(userId, organizationId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Organization");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetCurrentOrganization_withUserNotFound_thenException() {
        try {
            // setup
            Long userId = 1L, organizationId = 1L;

            // when
            Mockito.when(organizationService.findById(organizationId)).thenReturn(new Organization(String.valueOf(System.currentTimeMillis())));
            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // then
            userService.setCurrentOrganization(userId, organizationId);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "User");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test
    public void whenSetCurrentOrganization_thenOk() {
        // setup
        Long userId = 1L, organizationId = 1L, groupId = 2L;
        Organization organization = new Organization(String.valueOf(System.currentTimeMillis()));
        organization.setId(organizationId);
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");

        // when
        Mockito.when(organizationService.findById(organizationId)).thenReturn(organization);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(groupService.getGroupOfUserInOrganization(userId, organizationId)).thenReturn(groupId);
        Mockito.when(userRepository.save(Mockito.any())).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // then
        User response = userService.setCurrentOrganization(userId, organizationId);
        Assert.assertEquals(response.getOrganizationId(), organizationId);
        Assert.assertEquals(response.getGroupId(), groupId);
    }

    @Test
    public void whenSave_thenOk() {
        // setup
        User user = new User("test", "test" + System.currentTimeMillis(), "test@gmail.com", "123456789");

        // when
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // then
        Assert.assertEquals(userService.save(user), user);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetGroup_withUserNotFound_thenException() {
        try {
            // setup
            Long userId = 1L, groupId = 2L;
            List<String> roles = Collections.emptyList();

            // when
            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // then
            userService.setGroup(userId, groupId, roles);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "User");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetGroup_withGroupNotFound_thenException() {
        try {
            // setup
            Long userId = 1L, groupId = 2L;
            List<String> roles = Collections.emptyList();

            // when
            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
            Mockito.when(groupService.findById(groupId)).thenAnswer(invocationOnMock -> {
               throw new ResourceNotFoundException("Group", "id", groupId);
            });

            // then
            userService.setGroup(userId, groupId, roles);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Group");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test(expected = AppException.class)
    public void whenSetGroup_withRolesEmpty_thenException() {
        try {
            // setup
            Long userId = 1L, groupId = 2L;
            List<String> roles = Collections.emptyList();

            // when
            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
            Mockito.when(groupService.findById(groupId)).thenReturn(new Group());

            // then
            userService.setGroup(userId, groupId, roles);
        } catch (AppException ex) {
            Assert.assertEquals(ex.getMessage(), "Role list is empty");
            throw ex;
        }
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetGroup_withRoleNotFound_thenException() {
        try {
            // setup
            Long userId = 1L, groupId = 2L;
            List<String> roles = Collections.singletonList("test");
            List<Role> roleList = Collections.singletonList(new Role("role", "role"));

            // when
            Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
            Mockito.when(groupService.findById(groupId)).thenReturn(new Group());
            Mockito.when(roleService.findAll(0, Integer.MAX_VALUE)).thenReturn(new PagedResponse<>(roleList, 0, Integer.MAX_VALUE, 1, 1, true));

            // then
            userService.setGroup(userId, groupId, roles);
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Role");
            Assert.assertEquals(ex.getFieldName(), "code");
            throw ex;
        }
    }

    @Test
    public void whenSetGroup_withSetNew_thenOk() {
        // setup
        Long userId = 1L, groupId = 2L;
        List<String> roles = Collections.singletonList("test");
        List<Role> roleList = Collections.singletonList(new Role("test", "test"));

        // when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Mockito.when(groupService.findById(groupId)).thenReturn(new Group("test", null, new Organization(), 0L));
        Mockito.when(roleService.findAll(0, Integer.MAX_VALUE)).thenReturn(new PagedResponse<>(roleList, 0, Integer.MAX_VALUE, 1, 1, true));
        Mockito.when(userOrganizationRepository.getByUser_IdAndOrganizationId(userId, groupId)).thenReturn(null);

        // then
        userService.setGroup(userId, groupId, roles);
    }

    @Test
    public void whenSetGroup_withUpdateRoleOldNull_thenOk() {
        // setup
        Long userId = 1L, groupId = 2L, organizationId = 3L;
        List<String> roles = Collections.singletonList("test");
        List<Role> roleList = Collections.singletonList(new Role("test", "test"));

        // when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Mockito.when(groupService.findById(groupId)).thenReturn(new Group("test", null, new Organization(), 0L));
        Mockito.when(roleService.findAll(0, Integer.MAX_VALUE)).thenReturn(new PagedResponse<>(roleList, 0, Integer.MAX_VALUE, 1, 1, true));
        Mockito.when(userOrganizationRepository.getByUser_IdAndOrganizationId(userId, groupId)).thenReturn(new UserOrganization());
        Mockito.when(userRoleRepository.getByUserOrganization_Id(organizationId)).thenReturn(null);

        // then
        userService.setGroup(userId, groupId, roles);
    }

    @Test
    public void whenSetGroup_withUpdate_thenOk() {
        // setup
        Long userId = 1L, groupId = 2L, organizationId = 3L;
        List<String> roles = Collections.singletonList("test");
        List<Role> roleList = Collections.singletonList(new Role("test", "test"));

        // when
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        Mockito.when(groupService.findById(groupId)).thenReturn(new Group("test", null, new Organization(), 0L));
        Mockito.when(roleService.findAll(0, Integer.MAX_VALUE)).thenReturn(new PagedResponse<>(roleList, 0, Integer.MAX_VALUE, 1, 1, true));
        Mockito.when(userOrganizationRepository.getByUser_IdAndOrganizationId(userId, groupId)).thenReturn(new UserOrganization());
        Mockito.when(userRoleRepository.getByUserOrganization_Id(organizationId)).thenReturn(Collections.singletonList(new UserRole()));

        // then
        userService.setGroup(userId, groupId, roles);
    }

}
