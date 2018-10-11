package com.edium.service.core.controller;

import com.edium.library.model.UserPrincipal;
import com.edium.library.model.core.Role;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
import com.edium.library.model.core.UserRole;
import com.edium.library.repository.core.RoleRepository;
import com.edium.library.repository.core.UserOrganizationRepository;
import com.edium.library.repository.core.UserRoleRepository;
import com.edium.library.spring.OAuthHelper;
import com.edium.service.core.CoreServiceApplication;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.payload.SetGroupRequest;
import com.edium.service.core.service.GroupService;
import com.edium.service.core.service.OrganizationService;
import com.edium.service.core.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = CoreServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserOrganizationRepository userOrganizationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private OAuthHelper oAuthHelper;

    private final UserPrincipal ADMIN = new UserPrincipal(2L, "ADMIN", "", "", "",
            true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @Test
    public void whenGetUserById_withIdNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/user/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("User not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetUserById_withIdFound_thenOk() throws Exception {
        long timestamp = System.currentTimeMillis();
        User user = userService.save(new User("test" + timestamp, "test" + timestamp, timestamp + "@gmail.com", "12345678909"));

        mvc.perform(get("/api/user/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email", Matchers.equalTo(user.getEmail())))
                .andDo(print());
    }

    @Test
    public void whenGetCurrentGroupOfUser_withUserNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/user/" + Integer.MIN_VALUE + "/currentGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("User not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetCurrentGroupOfUser_withGroupNotFound_thenBadRequest() throws Exception {
        long timestamp = System.currentTimeMillis();

        User user = new User("test" + timestamp, "test" + timestamp, timestamp + "@gmail.com", "12345678909");
        user.setGroupId((long) Integer.MIN_VALUE);

        User savedUser = userService.save(user);

        mvc.perform(get("/api/user/" + savedUser.getId() + "/currentGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetCurrentGroupOfUser_thenOk() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));

        Group group = groupService.save(new Group(timestamp, null, organization, 0L));

        User user = new User(timestamp, timestamp, timestamp + "@gmail.com", "12345678909");
        user.setGroupId(group.getId());

        User savedUser = userService.save(user);

        mvc.perform(get("/api/user/" + savedUser.getId() + "/currentGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.equalTo(group.getName())))
                .andDo(print());
    }

    @Test
    public void whenGetGroupsOfUser_withPageNegative_thenBadRequest() throws Exception {
        mvc.perform(get("/api/user/1/groups?page=-1&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Page number cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetGroupsOfUser_withSizeNegative_thenBadRequest() throws Exception {
        mvc.perform(get("/api/user/1/groups?page=0&size=-10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Page size cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetGroupsOfUser_withUserNotFound_thenOk() throws Exception {
        mvc.perform(get("/api/user/" + Integer.MIN_VALUE + "/groups?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements", Matchers.equalTo(0)))
                .andDo(print());
    }

    @Test
    public void whenGetGroupsOfUser_thenOk() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));

        Group group = groupService.save(new Group(timestamp, null, organization, 0L));

        User user = userService.save(new User(timestamp, timestamp, timestamp + "@gmail.com", "12345678909"));

        userOrganizationRepository.save(new UserOrganization(user, organization.getId(), group.getId()));

        mvc.perform(get("/api/user/" + user.getId() + "/groups?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.content[0].id", Matchers.equalTo(group.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenSetCurrentOrganization_withOrgNotFound_thenBadRequest() throws Exception {
        mvc.perform(put("/api/user/" + Integer.MIN_VALUE + "/setCurrentOrganization/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Organization not found with id")))
                .andDo(print());
    }

    @Test
    public void whenSetCurrentOrganization_withUserNotFound_thenBadRequest() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));

        mvc.perform(put("/api/user/" + Integer.MIN_VALUE + "/setCurrentOrganization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("User not found with id")))
                .andDo(print());
    }

    @Test
    public void whenSetCurrentOrganization_withUserNotInOrg_thenBadRequest() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));
        User user = userService.save(new User(timestamp, timestamp, timestamp + "@gmail.com", "12345678909"));

        mvc.perform(put("/api/user/" + user.getId() + "/setCurrentOrganization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("User does not belong to the organization")))
                .andDo(print());
    }

    @Test
    public void whenSetCurrentOrganization_withGroupNotFound_thenBadRequest() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));
        User user = userService.save(new User(timestamp, timestamp, timestamp + "@gmail.com", "12345678909"));
        userOrganizationRepository.save(new UserOrganization(user, organization.getId(), (long) Integer.MIN_VALUE));

        mvc.perform(put("/api/user/" + user.getId() + "/setCurrentOrganization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenSetCurrentOrganization_thenOk() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));
        Group group = groupService.save(new Group(timestamp, null, organization, 0L));
        User user = userService.save(new User(timestamp, timestamp, timestamp + "@gmail.com", "12345678909"));
        userOrganizationRepository.save(new UserOrganization(user, organization.getId(), group.getId()));

        mvc.perform(put("/api/user/" + user.getId() + "/setCurrentOrganization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withGroupIdNull_thenBadRequest() throws Exception {
        SetGroupRequest request = new SetGroupRequest(null, Collections.singletonList("test"));

        mvc.perform(post("/api/user/1/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("groupId")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be null")))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withRoleNull_thenBadRequest() throws Exception {
        SetGroupRequest request = new SetGroupRequest(1L, null);

        mvc.perform(post("/api/user/1/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("roles")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be empty")))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withRoleEmpty_thenBadRequest() throws Exception {
        SetGroupRequest request = new SetGroupRequest(1L, Collections.emptyList());

        mvc.perform(post("/api/user/1/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("roles")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be empty")))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withUserNotFound_thenBadRequest() throws Exception {
        SetGroupRequest request = new SetGroupRequest(1L, Collections.singletonList("test"));

        mvc.perform(post("/api/user/" + Integer.MIN_VALUE + "/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("User not found with id")))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withGroupNotFound_thenBadRequest() throws Exception {
        long timestamp = System.currentTimeMillis();
        User user = userService.save(new User("test" + timestamp, "test" + timestamp, timestamp + "@gmail.com", "12345678909"));

        SetGroupRequest request = new SetGroupRequest((long) Integer.MIN_VALUE, Collections.singletonList("test"));

        mvc.perform(post("/api/user/" + user.getId() + "/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withRoleNotFound_thenBadRequest() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));
        Group group = groupService.save(new Group(timestamp, null, organization, 0L));
        User user = userService.save(new User("test" + timestamp, "test" + timestamp, timestamp + "@gmail.com", "12345678909"));

        SetGroupRequest request = new SetGroupRequest(group.getId(), Collections.singletonList(timestamp));

        mvc.perform(post("/api/user/" + user.getId() + "/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role not found with code")))
                .andDo(print());
    }

    @Test
    public void whenSetGroup_withSaveAndUpdate_thenOk() throws Exception {
        // Assign user to Organization
        String timestamp = String.valueOf(System.currentTimeMillis());

        Organization organization = organizationService.save(new Organization(timestamp));
        Group group = groupService.save(new Group(timestamp, null, organization, 0L));
        User user = userService.save(new User("test" + timestamp, "test" + timestamp, timestamp + "@gmail.com", "12345678909"));

        roleRepository.save(new Role(timestamp, timestamp));

        SetGroupRequest request = new SetGroupRequest(group.getId(), Collections.singletonList(timestamp));

        mvc.perform(post("/api/user/" + user.getId() + "/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());

        UserOrganization userOrganization = userOrganizationRepository.getByUser_IdAndOrganizationId(user.getId(), organization.getId());
        Assert.assertTrue(userOrganization != null);
        Assert.assertEquals(userOrganization.getGroupId(), group.getId());

        List<UserRole> roleList = userRoleRepository.getByUserOrganization_Id(userOrganization.getId());
        Assert.assertTrue(roleList != null && roleList.size() == 1);
        Assert.assertEquals(roleList.get(0).getRole().getCode(), timestamp);

        // Update Group for user
        String roleNew = "test_" + timestamp;
        roleRepository.save(new Role(roleNew, roleNew));
        Group group1 = groupService.save(new Group(timestamp + "_1", group.getId(), organization, 1L));

        SetGroupRequest request1 = new SetGroupRequest(group1.getId(), Collections.singletonList(roleNew));

        mvc.perform(post("/api/user/" + user.getId() + "/setGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());

        UserOrganization userOrganization1 = userOrganizationRepository.getByUser_IdAndOrganizationId(user.getId(), organization.getId());
        Assert.assertTrue(userOrganization1 != null);
        Assert.assertEquals(userOrganization1.getGroupId(), group1.getId());

        List<UserRole> roleList1 = userRoleRepository.getByUserOrganization_Id(userOrganization.getId());
        Assert.assertTrue(roleList1 != null && roleList1.size() == 1);
        Assert.assertEquals(roleList1.get(0).getRole().getCode(), roleNew);
    }

    @Test
    public void whenGetByUsernameOrEmail_withUserOrEmailNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/user/getByUsernameOrEmail/" + System.currentTimeMillis())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("User not found with username or email")))
                .andDo(print());
    }

    @Test
    public void whenGetByUsernameOrEmail_withUserFound_thenOk() throws Exception {
        long timestamp = System.currentTimeMillis();
        User user = userService.save(new User("test" + timestamp, "test_" + timestamp, timestamp + "@gmail.com", "12345678909"));

        mvc.perform(get("/api/user/getByUsernameOrEmail/" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(user.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetByUsernameOrEmail_withEmailFound_thenOk() throws Exception {
        long timestamp = System.currentTimeMillis();
        User user = userService.save(new User("test" + timestamp, "test_" + timestamp, timestamp + "@gmail.com", "12345678909"));

        mvc.perform(get("/api/user/getByUsernameOrEmail/" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(user.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetMe_thenOk() throws Exception {
        mvc.perform(get("/api/user/me")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(ADMIN.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenCheckUsernameAvailability_withUsernameNotExist_thenOk() throws Exception {
        mvc.perform(get("/api/user/checkUsernameAvailability?username=" + System.currentTimeMillis())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    public void whenCheckUsernameAvailability_withUsernameExist_thenOk() throws Exception {
        long timestamp = System.currentTimeMillis();
        User user = userService.save(new User("test" + timestamp, "test_" + timestamp, timestamp + "@gmail.com", "12345678909"));

        mvc.perform(get("/api/user/checkUsernameAvailability?username=" + user.getUsername())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(print());
    }

    @Test
    public void whenCheckEmailAvailability_withEmailNotExist_thenOk() throws Exception {
        mvc.perform(get("/api/user/checkEmailAvailability?email=" + System.currentTimeMillis())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(print());
    }

    @Test
    public void whenCheckEmailAvailability_withEmailExist_thenOk() throws Exception {
        long timestamp = System.currentTimeMillis();
        User user = userService.save(new User("test" + timestamp, "test_" + timestamp, timestamp + "@gmail.com", "12345678909"));

        mvc.perform(get("/api/user/checkEmailAvailability?email=" + user.getEmail())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(print());
    }
}
