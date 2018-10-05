package com.edium.service.core.controller;

import com.edium.library.config.AuditingConfig;
import com.edium.library.model.UserPrincipal;
import com.edium.library.spring.OAuthHelper;
import com.edium.service.core.CoreServiceApplication;
import com.edium.service.core.model.Organization;
import com.edium.service.core.service.OrganizationService;
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

}
