package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.UserPrincipal;
import com.edium.library.spring.OAuthHelper;
import com.edium.library.util.BaseX;
import com.edium.service.core.CoreServiceApplication;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.payload.GroupRequest;
import com.edium.service.core.service.GroupService;
import com.edium.service.core.service.OrganizationService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = CoreServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class GroupControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private OAuthHelper oAuthHelper;

    private final UserPrincipal ADMIN = new UserPrincipal(2L, "", "", "", "",
            true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @Test
    public void whenGetAllGroup_withPageNegative_thenBadRequest() throws Exception {
        mvc.perform(get("/api/group?page=-1&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page number cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetAllGroup_withSizeNegative_thenBadRequest() throws Exception {
        mvc.perform(get("/api/group?page=0&size=-1")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.equalTo("Page size cannot be less than zero.")))
                .andDo(print());
    }

    @Test
    public void whenGetAllGroup_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));

        mvc.perform(get("/api/group?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalElements", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.content[0].id", Matchers.equalTo(group.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetGroupById_withIdNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/group/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetGroupById_withIdFound_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));

        mvc.perform(get("/api/group/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", Matchers.equalTo(group.getName())))
                .andDo(print());
    }

    @Test
    public void whenDeleteById_withIdNotFound_thenBadRequest() throws Exception {
        mvc.perform(delete("/api/group/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenDeleteById_withIdFound_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));

        mvc.perform(delete("/api/group/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", Matchers.equalTo(true)))
                .andDo(print());
        try {
            groupService.findById(group.getId());
            Assert.fail();
        } catch (ResourceNotFoundException ex) {
        }
    }

    @Test
    public void whenCreateGroup_withNameBlank_thenBadRequest() throws Exception {
        GroupRequest groupRequest = new GroupRequest("", 1L, null);

        mvc.perform(post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("name")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenCreateGroup_withOrganizationIdNull_thenBadRequest() throws Exception {
        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), null, null);

        mvc.perform(post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("organizationId")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be null")))
                .andDo(print());
    }

    @Test
    public void whenCreateGroup_withOrganizationNotFound_thenBadRequest() throws Exception {
        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), Long.MIN_VALUE, null);

        mvc.perform(post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Organization not found with id")))
                .andDo(print());
    }

    @Test
    public void whenCreateGroup_withParentGroupNotFound_thenBadRequest() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), Long.MIN_VALUE);

        mvc.perform(post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenCreateGroup_withNoParent_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), null);

        MvcResult mvcResult = mvc.perform(post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print()).andReturn();
        Group group = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Group.class);

        BaseX base62 = new BaseX();
        String encode = base62.encode(BigInteger.valueOf(group.getId()));

        Assert.assertEquals(group.getEncodedId(), base62.encode(BigInteger.valueOf(encode.length())) + encode);
        Assert.assertEquals(group.getEncodedRootPath(), group.getEncodedId());
        Assert.assertEquals(group.getRootPath(), "/" + group.getId());
    }

    @Test
    public void whenCreateGroup_withParent_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group parentGroup = groupService.save(new Group("parent" + System.currentTimeMillis(), null, organization, 0L));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), parentGroup.getId());

        MvcResult mvcResult = mvc.perform(post("/api/group")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print()).andReturn();
        Group group = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Group.class);

        BaseX base62 = new BaseX();
        String encode = base62.encode(BigInteger.valueOf(group.getId()));

        Assert.assertEquals(group.getEncodedId(), base62.encode(BigInteger.valueOf(encode.length())) + encode);
        Assert.assertEquals(group.getEncodedRootPath(), parentGroup.getEncodedId() + "-" + group.getEncodedId());
        Assert.assertEquals(group.getRootPath(), "/" + parentGroup.getId() + "/" + group.getId());
    }

    @Test
    public void whenUpdateGroup_withNameBlank_thenBadRequest() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("parent" + System.currentTimeMillis(), null, organization, 0L));

        GroupRequest groupRequest = new GroupRequest("", organization.getId(), null);

        mvc.perform(put("/api/group/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("name")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be blank")))
                .andDo(print());
    }

    @Test
    public void whenUpdateGroup_withOrganizationIdNull_thenBadRequest() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("parent" + System.currentTimeMillis(), null, organization, 0L));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), null, null);

        mvc.perform(put("/api/group/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.equalTo("organizationId")))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.equalTo("must not be null")))
                .andDo(print());
    }

    @Test
    public void whenUpdateGroup_withOrganizationNotFound_thenBadRequest() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("parent" + System.currentTimeMillis(), null, organization, 0L));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), Long.MIN_VALUE, null);

        mvc.perform(put("/api/group/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Organization not found with id")))
                .andDo(print());
    }

    @Test
    public void whenUpdateGroup_withGroupNotFound_thenBadRequest() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), null);

        mvc.perform(put("/api/group/" + Integer.MIN_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenUpdateGroup_withParentGroupNotFound_thenBadRequest() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group group = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), Long.MIN_VALUE);

        mvc.perform(put("/api/group/" + group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenUpdateGroup_withNoParent_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group groupChild1 = groupService.save(new Group("test" + System.currentTimeMillis(), groupParent.getId(), organization, 1L));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), null);

        mvc.perform(put("/api/group/" + groupChild1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print());

        Group group = groupService.findById(groupChild1.getId());

        BaseX base62 = new BaseX();
        String encode = base62.encode(BigInteger.valueOf(group.getId()));

        Assert.assertEquals(group.getEncodedId(), base62.encode(BigInteger.valueOf(encode.length())) + encode);
        Assert.assertEquals(group.getEncodedRootPath(), group.getEncodedId());
        Assert.assertEquals(group.getRootPath(), "/" + group.getId());
    }

    @Test
    public void whenUpdateGroup_withParent_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group groupChild1 = groupService.save(new Group("test" + System.currentTimeMillis(), groupParent.getId(), organization, 1L));
        Group groupChild2 = groupService.save(new Group("test" + System.currentTimeMillis(), groupParent.getId(), organization, 1L));

        GroupRequest groupRequest = new GroupRequest("test" + System.currentTimeMillis(), organization.getId(), groupChild1.getId());

        mvc.perform(put("/api/group/" + groupChild2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupRequest))
                .with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print());

        Group group = groupService.findById(groupChild2.getId());

        BaseX base62 = new BaseX();
        String encode = base62.encode(BigInteger.valueOf(group.getId()));

        Assert.assertEquals(group.getEncodedId(), base62.encode(BigInteger.valueOf(encode.length())) + encode);
        Assert.assertEquals(group.getEncodedRootPath(), groupParent.getEncodedId() + "-" + groupChild1.getEncodedId() + "-" + group.getEncodedId());
        Assert.assertEquals(group.getRootPath(), "/" + groupParent.getId() + "/" + groupChild1.getId() + "/" + group.getId());
    }

    @Test
    public void whenGetParentGroup_withIdNotFound_thenOk() throws Exception {
        mvc.perform(get("/api/group/" + Integer.MIN_VALUE + "/parent")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    public void whenGetParentGroup_withIdFoundNoParent_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));

        mvc.perform(get("/api/group/" + groupParent.getId() + "/parent")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    public void whenGetParentGroup_withIdFoundHasParent_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group groupChild1 = groupService.save(new Group("test" + System.currentTimeMillis(), groupParent.getId(), organization, 1L));

        mvc.perform(get("/api/group/" + groupChild1.getId() + "/parent")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.equalTo(groupParent.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetChildren_withIdNotFound_thenOk() throws Exception {
        mvc.perform(get("/api/group/" + Integer.MIN_VALUE + "/children")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    @Test
    public void whenGetChildren_withIdFoundNoChildren_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));

        mvc.perform(get("/api/group/" + groupParent.getId() + "/children")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    @Test
    public void whenGetChildren_withIdFoundHasChildren_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group groupChild1 = groupService.save(new Group("test" + System.currentTimeMillis(), groupParent.getId(), organization, 1L));

        mvc.perform(get("/api/group/" + groupParent.getId() + "/children")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(groupChild1.getId().intValue())))
                .andDo(print());
    }

    @Test
    public void whenGetTree_withIdNotFound_thenBadRequest() throws Exception {
        mvc.perform(get("/api/group/" + Integer.MIN_VALUE + "/tree")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Group not found with id")))
                .andDo(print());
    }

    @Test
    public void whenGetTree_withIdFound_thenOk() throws Exception {
        Organization organization = organizationService.save(new Organization("test" + System.currentTimeMillis()));
        Group groupParent = groupService.save(new Group("test" + System.currentTimeMillis(), null, organization, 0L));
        Group groupChild1 = groupService.save(new Group("test" + System.currentTimeMillis(), groupParent.getId(), organization, 1L));
        Group groupChild2 = groupService.save(new Group("test" + System.currentTimeMillis(), groupChild1.getId(), organization, 2L));

        mvc.perform(get("/api/group/" + groupParent.getId() + "/tree")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(3)))
                .andExpect(jsonPath("$[*].id", Matchers.hasItem(groupChild1.getId().intValue())))
                .andExpect(jsonPath("$[*].id", Matchers.hasItem(groupChild2.getId().intValue())))
                .andDo(print());
    }

}
