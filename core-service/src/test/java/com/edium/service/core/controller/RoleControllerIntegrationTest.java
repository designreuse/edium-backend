package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.UserPrincipal;
import com.edium.library.model.core.Role;
import com.edium.library.service.RoleService;
import com.edium.library.spring.OAuthHelper;
import com.edium.service.core.CoreServiceApplication;
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
import org.springframework.test.annotation.Rollback;
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
public class RoleControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private OAuthHelper oAuthHelper;

    private final UserPrincipal ADMIN = new UserPrincipal(2L, "", "", "", "",
            true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @Test
    public void whenGetAllRole_withPageNegative_thenBadRequest() throws Exception {
        mvc.perform(get("/api/role?page=-1&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page number cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetAllRole_withSizeNegative_thenBadRequest() throws Exception {
        mvc.perform(get("/api/role?page=0&size=-1")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page size cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetAllRole_thenOk() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis()));

        mvc.perform(get("/api/role?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.content[0].id", Matchers.equalTo(role.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetRoleById_withIdNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/role/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetRoleById_withIdFound_thenOk() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis()));

        mvc.perform(get("/api/role/" + role.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", Matchers.equalTo(role.getCode())))
                .andDo(print());
    }

    @Test
    public void whenGetRoleByCode_withCodeNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/role/getByCode/" + System.currentTimeMillis())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role not found with code")))
                .andDo(print());
    }

    @Test
    public void whenGetRoleByCode_withCodeFound_thenOk() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis()));

        mvc.perform(get("/api/role/getByCode/" + role.getCode())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.label", Matchers.equalTo(role.getLabel())))
                .andDo(print());
    }

    @Test
    public void whenDeleteById_withIdNotFound_thenBadRequest() throws Exception {
        mvc.perform(delete("/api/role/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role not found with id")))
                .andDo(print());
    }

    @Test
    public void whenDeleteById_withIdFound_thenOk() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis()));

        mvc.perform(delete("/api/role/" + role.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());
        try {
            roleService.findById(role.getId());
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }
    }

    @Test
    public void whenDeleteByCode_withCodeNotFound_thenBadRequest() throws Exception {
        mvc.perform(delete("/api/role/deleteByCode/" + System.currentTimeMillis())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role not found with code")))
                .andDo(print());
    }

    @Test
    public void whenDeleteByCode_withCodeFound_thenOk() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test" + System.currentTimeMillis()));

        mvc.perform(delete("/api/role/deleteByCode/" + role.getCode())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());
        try {
            roleService.findByCode(role.getCode());
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }
    }

    @Test
    public void whenCreateRole_withCodeBlank_thenBadRequest() throws Exception {
        Role role = new Role("", String.valueOf(System.currentTimeMillis()));

        mvc.perform(post("/api/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("code")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenCreateRole_withLabelBlank_thenBadRequest() throws Exception {
        Role role = new Role(String.valueOf(System.currentTimeMillis()), "");

        mvc.perform(post("/api/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("label")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenCreateRole_withCodeDuplicate_thenBadRequest() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test"));

        Role newRole = new Role(role.getCode(), "test1");

        mvc.perform(post("/api/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role existed with code : '" + role.getCode() + "'")))
                .andDo(print());
    }

    @Test
    public void whenCreateRole_thenOk() throws Exception {
        Role newRole = new Role("test_" + System.currentTimeMillis(), "test_" + System.currentTimeMillis());

        mvc.perform(post("/api/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print());
    }

    @Test
    public void whenUpdateRole_withCodeBlank_thenBadRequest() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test"));

        Role updateRole = new Role("", String.valueOf(System.currentTimeMillis()));

        mvc.perform(put("/api/role/" + role.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRole))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("code")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenUpdateRole_withLabelBlank_thenBadRequest() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test"));

        Role updateRole = new Role(String.valueOf(System.currentTimeMillis()), "");

        mvc.perform(put("/api/role/" + role.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRole))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("label")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void whenUpdateRole_withCodeDuplicate_thenBadRequest() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test"));
        Role role1 = roleService.save(new Role("test" + System.currentTimeMillis(), "test"));

        Role newRole = new Role(role.getCode(), "test1");

        mvc.perform(put("/api/role/" + role1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Role existed with code : '" + role.getCode() + "'")))
                .andDo(print());
    }

    @Test
    public void whenUpdateRole_thenOk() throws Exception {
        Role role = roleService.save(new Role("test" + System.currentTimeMillis(), "test"));

        Role newRole = new Role("test_" + System.currentTimeMillis(), "test1");

        mvc.perform(put("/api/role/" + role.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newRole))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code", Matchers.equalTo(newRole.getCode())))
                .andExpect(jsonPath("$.label", Matchers.equalTo(newRole.getLabel())))
                .andDo(print());
    }

}
