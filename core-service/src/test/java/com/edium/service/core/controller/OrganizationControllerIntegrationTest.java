package com.edium.service.core.controller;

import com.edium.library.config.AuditingConfig;
import com.edium.library.model.UserPrincipal;
import com.edium.library.model.core.Role;
import com.edium.library.model.core.User;
import com.edium.library.service.RoleService;
import com.edium.library.spring.OAuthHelper;
import com.edium.service.core.CoreServiceApplication;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.service.GroupService;
import com.edium.service.core.service.OrganizationService;
import com.edium.service.core.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Before;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = CoreServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrganizationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OAuthHelper oAuthHelper;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private UserPrincipal USER, ADMIN;

    @Before
    public void setup() {
        USER = new UserPrincipal(AuditingConfig.USER_ID_TEST, "", "", "", "", true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
        ADMIN = new UserPrincipal(2L, "", "", "", "", true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    public void whenGetOrganizationById_withIdNotFound_thenException() throws Exception {
        mvc.perform(get("/api/organization/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Organization not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetOrganizationById_withIdFoundAndHasPermission_thenReturn() throws Exception {
        Organization organization = organizationService.save(new Organization("test"));

        mvc.perform(get("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.equalTo("test")))
                .andDo(print());

        UserPrincipal userPrincipal1 = new UserPrincipal(1L, "", "", "", "", true, organization.getId(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        mvc.perform(get("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(userPrincipal1)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.equalTo("test")))
                .andDo(print());
    }

    @Test
    public void whenGetOrganizationById_withIdFoundAndNoPermission_roleUser_thenReturn() throws Exception {
        Organization organization = organizationService.save(new Organization("test"));

        mvc.perform(get("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(USER)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AccessDeniedException")))
                .andDo(print());
    }

    @Test
    public void whenDelete_withIdNotFound_thenException() throws Exception {
        mvc.perform(delete("/api/organization/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Organization not found with id")))
                .andDo(print());
    }

    @Test
    public void whenDelete_withIdFound_roleAdmin_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test"));

        mvc.perform(delete("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void whenDelete_withIdFound_roleUser_noPermission_thenException() throws Exception {
        Organization organization = organizationService.save(new Organization("test"));
        organization.setCreatedBy(-1L);

        UserPrincipal userPrincipal = new UserPrincipal(1L, "", "", "", "", true, organization.getId(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        mvc.perform(delete("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(userPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AccessDeniedException")))
                .andDo(print());
    }

    @Test
    public void whenDelete_withIdFound_roleUser_hasPermission_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test"));

        UserPrincipal userPrincipal = new UserPrincipal(AuditingConfig.USER_ID_TEST, "", "", "", "", true, organization.getId(), Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        mvc.perform(delete("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(userPrincipal)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());
    }

    @Test
    public void whenCreateOrg_withNameBlank_thenException() throws Exception {
        Organization organization = new Organization("");

        mvc.perform(post("/api/organization")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(organization)).with(oAuthHelper.bearerToken(USER)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("name")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenCreateOrg_thenOk() throws Exception {
        Organization organization = new Organization("test");

        mvc.perform(post("/api/organization")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(organization)).with(oAuthHelper.bearerToken(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print());
    }

    @Test
    public void whenUpdateOrg_withIdNotFound_thenException() throws Exception {
        mvc.perform(put("/api/organization/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Organization("test")))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Organization not found with id")))
                .andDo(print());
    }

    @Test
    public void whenUpdateOrg_withNameBlank_thenException() throws Exception {
        mvc.perform(put("/api/organization/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new Organization("")))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("name")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenUpdateOrg_withRoleAdmin_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        Organization organizationUpdate = new Organization("test_" + System.currentTimeMillis());

        mvc.perform(put("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationUpdate))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.equalTo(organizationUpdate.getName())))
                .andDo(print());
    }

    @Test
    public void whenUpdateOrg_withRoleUser_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        Organization organizationUpdate = new Organization("test_" + System.currentTimeMillis());

        mvc.perform(put("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationUpdate))
                .with(oAuthHelper.bearerToken(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.equalTo(organizationUpdate.getName())))
                .andDo(print());
    }

    @Test
    public void whenUpdateOrg_withRoleUser_thenException() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        Organization organizationUpdate = new Organization("test_" + System.currentTimeMillis());

        UserPrincipal userPrincipal = new UserPrincipal(AuditingConfig.USER_ID_TEST * 10, "", "", "", "", true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

        mvc.perform(put("/api/organization/" + organization.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(organizationUpdate))
                .with(oAuthHelper.bearerToken(userPrincipal)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AccessDeniedException")))
                .andDo(print());
    }

    @Test
    public void whenGetAllOrganizations_withPageNegative_thenException() throws Exception {
        mvc.perform(get("/api/organization?page=-1&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page number cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetAllOrganizations_withSizeNegative_thenException() throws Exception {
        mvc.perform(get("/api/organization?page=0&size=-1")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page size cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetAllOrganizations_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        mvc.perform(get("/api/organization?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", Matchers.equalTo(organization.getId().intValue())))
                .andExpect(jsonPath("$.totalElements", Matchers.equalTo(1)))
                .andDo(print());
    }

    @Test
    public void whenGetTreeGroup_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        Organization organization1 = organizationService.save(new Organization("test_" + System.currentTimeMillis()));

        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group group1 = groupService.save(new Group("test" + System.currentTimeMillis(), group.getId(), organization, 1L));

        groupService.save(new Group("test_" + System.currentTimeMillis(), null, organization1, 0L));

        mvc.perform(get("/api/organization/" + organization.getId() + "/treeGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(2)))
                .andExpect(jsonPath("$[*].id", Matchers.hasItem(group.getId().intValue())))
                .andExpect(jsonPath("$[*].id", Matchers.hasItem(group1.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetRootGroup_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        groupService.save(new Group("test" + System.currentTimeMillis(), group.getId(), organization, 1L));

        mvc.perform(get("/api/organization/" + organization.getId() + "/rootGroup")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[*].id", Matchers.hasItem(group.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetUserOfOrganization_withPageNegative_thenException() throws Exception {
        mvc.perform(get("/api/organization/1/user?page=-1&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page number cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetUserOfOrganization_withSizeNegative_thenException() throws Exception {
        mvc.perform(get("/api/organization/1/user?page=0&size=-1")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page size cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetUserOfOrganization_withSizeNegative_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Organization organization1 = organizationService.save(new Organization("test_" + System.currentTimeMillis()));

        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group group1 = groupService.save(new Group("test" + System.currentTimeMillis(), group.getId(), organization, 1L));

        Group group2 = groupService.save(new Group("test_" + System.currentTimeMillis(), null, organization1, 0L));

        User user = userService.save(new User("user123", "username123", "user123@gmail.com", "123456789"));
        User user1 = userService.save(new User("user1234", "username1234", "user1234@gmail.com", "123456789"));
        User user2 = userService.save(new User("user12345", "username12345", "user12345@gmail.com", "123456789"));

        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis()));

        userService.setGroup(user.getId(), group.getId(), Collections.singletonList(role.getCode()));
        userService.setGroup(user1.getId(), group1.getId(), Collections.singletonList(role.getCode()));
        userService.setGroup(user2.getId(), group2.getId(), Collections.singletonList(role.getCode()));

        mvc.perform(get("/api/organization/" + organization.getId() + "/user?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", Matchers.hasSize(2)))
                .andExpect(jsonPath("$.content[*].id", Matchers.hasItem(user.getId().intValue())))
                .andExpect(jsonPath("$.content[*].id", Matchers.hasItem(user1.getId().intValue())))
                .andDo(print());
    }
}
