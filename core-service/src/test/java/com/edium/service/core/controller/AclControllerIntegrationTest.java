package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.ResourceTypeCode;
import com.edium.library.model.SubjectTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.payload.AclEntryRequest;
import com.edium.library.payload.GroupDTO;
import com.edium.library.service.AclService;
import com.edium.service.core.CoreServiceApplication;
import com.edium.service.core.service.AclServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = CoreServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AclControllerIntegrationTest {

    private static final GroupDTO groupDTO = new GroupDTO(2L, "test", null, 0L, "/1/2");

    @TestConfiguration
    static class AclControllerIntegrationTestContextConfiguration {

        @Bean
        public AclServiceImpl aclService() {
            return new AclServiceImpl() {
                @Override
                public GroupDTO getGroupOfUser(Long userId) {
                    return groupDTO;
                }
            };
        }
    }

    @Autowired
    private MockMvc mvc;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AclService aclService;

    @Test
    public void givenResourceType_whenCRUD_thenStatus200() throws Exception {
        // setup
        long timestamp = System.currentTimeMillis();
        AclResourceType aclResourceType = new AclResourceType(1L, "integration_test_" + timestamp);

        // create
        mvc.perform(post("/api/acl/resourceType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is(aclResourceType.getType())));

        // getByType
        mvc.perform(get("/api/acl/resourceType/" + aclResourceType.getType())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is(aclResourceType.getType())));

        // getAll
        mvc.perform(get("/api/acl/resourceType")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].type", Matchers.hasItem(aclResourceType.getType())));

        // delete
        mvc.perform(delete("/api/acl/resourceType/" + aclResourceType.getType())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));

        // getByType
        mvc.perform(get("/api/acl/resourceType/" + aclResourceType.getType())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void givenResourceType_whenCreate_withTypeBlank_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(1L, "");

        // create
        mvc.perform(post("/api/acl/resourceType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("type")))
                .andDo(print());
    }

    @Test
    public void givenResourceType_whenCreate_withTypeNull_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(1L, null);

        // create
        mvc.perform(post("/api/acl/resourceType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("type")))
                .andDo(print());
    }

    @Test
    public void givenResourceType_whenCreate_withIdNull_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(null, "test");

        // create
        mvc.perform(post("/api/acl/resourceType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void whenGetResourceByType_withTypeNotFound_thenException() throws Exception {
        // getByType
        mvc.perform(get("/api/acl/resourceType/no_exist_123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenDeleteResourceByType_withTypeNotFound_thenException() throws Exception {
        // delete
        mvc.perform(delete("/api/acl/resourceType/no_exist_123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void givenSubjectType_whenCRUD_thenStatus200() throws Exception {
        // setup
        long timestamp = System.currentTimeMillis();
        AclSubjectType aclSubjectType = new AclSubjectType(1L, "integration_test_" + timestamp);

        // create
        mvc.perform(post("/api/acl/subjectType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclSubjectType)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is(aclSubjectType.getType())));

        // getByType
        mvc.perform(get("/api/acl/subjectType/" + aclSubjectType.getType())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.type", is(aclSubjectType.getType())));

        // getAll
        mvc.perform(get("/api/acl/subjectType")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].type", Matchers.hasItem(aclSubjectType.getType())));

        // delete
        mvc.perform(delete("/api/acl/subjectType/" + aclSubjectType.getType())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));

        // getByType
        mvc.perform(get("/api/acl/subjectType/" + aclSubjectType.getType())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void givenSubjectType_whenCreate_withTypeBlank_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(1L, "");

        // create
        mvc.perform(post("/api/acl/subjectType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("type")))
                .andDo(print());
    }

    @Test
    public void givenSubjectType_whenCreate_withTypeNull_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(1L, null);

        // create
        mvc.perform(post("/api/acl/subjectType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("type")))
                .andDo(print());
    }

    @Test
    public void givenSubjectType_whenCreate_withIdNull_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(null, "test");

        // create
        mvc.perform(post("/api/acl/subjectType")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(aclResourceType)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void whenGetSubjectByType_withTypeNotFound_thenException() throws Exception {
        // getByType
        mvc.perform(get("/api/acl/subjectType/no_exist_123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenDeleteSubjectByType_withTypeNotFound_thenException() throws Exception {
        // delete
        mvc.perform(delete("/api/acl/subjectType/no_exist_123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andDo(print());
    }

    @Test
    public void givenEntry_whenCRUD_thenStatus200() throws Exception {
        // setup
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create subject type
        AclSubjectType aclSubjectType = new AclSubjectType(1L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(aclSubjectType);

        // create
        MvcResult response = mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.resourceId", is(entryRequest.getResourceId().intValue())))
                .andReturn();
        AclEntry entryInsert = objectMapper.readValue(response.getResponse().getContentAsString(), AclEntry.class);

        // getById
        mvc.perform(get("/api/acl/" + entryInsert.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(entryInsert.getId().intValue())));

        // update
        AclEntryRequest entryRequestUpdate = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequestUpdate.setReadGrant(false);
        entryRequestUpdate.setWriteGrant(true);
        mvc.perform(put("/api/acl/" + entryInsert.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequestUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.writeGrant", is(entryRequestUpdate.isWriteGrant())))
                .andExpect(jsonPath("$.readGrant", is(entryRequestUpdate.isReadGrant())))
                .andReturn();

        // delete
        mvc.perform(delete("/api/acl/" + entryInsert.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));

        // getById
        mvc.perform(get("/api/acl/" + entryInsert.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void whenGetEntryById_withIdNotFound_thenException() throws Exception {
        // getById
        mvc.perform(get("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Entry not found with id")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withResourceTypeNotFound_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest("no_exist_" + System.currentTimeMillis(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withSubjectTypeNotFound_thenException() throws Exception {
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        // create resource type
        aclService.saveResourceType(aclResourceType);

        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, "no_exist_" + System.currentTimeMillis(), 2L);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withResourceTypeBlank_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest("", 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("resourceType")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withSubjectTypeBlank_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, "", 2L);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("subjectType")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withResourceIdNull_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), null, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be null")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("resourceId")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withSubjectIdNull_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), null);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be null")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("subjectId")))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withNoGrant_thenNoInsertAndReturnEmptyEntry() throws Exception {
        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create subject type
        AclSubjectType aclSubjectType = new AclSubjectType(1L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(aclSubjectType);

        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(false);
        entryRequest.setWriteGrant(false);
        entryRequest.setDeleteGrant(false);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.isEmptyOrNullString()))
                .andDo(print());
    }

    @Test
    public void whenCreateEntry_withDuplicateEntry_thenException() throws Exception {
        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create subject type
        AclSubjectType aclSubjectType = new AclSubjectType(1L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(aclSubjectType);

        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andDo(print());

        AclEntryRequest entryRequest1 = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest1.setWriteGrant(true);

        mvc.perform(post("/api/acl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest1)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("ViolationException")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withIdNotFound_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create subject type
        AclSubjectType aclSubjectType = new AclSubjectType(1L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(aclSubjectType);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Entry not found with id")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withResourceTypeBlank_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest("", 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("resourceType")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withSubjectTypeBlank_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, "", 2L);
        entryRequest.setReadGrant(true);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("subjectType")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withResourceIdNull_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), null, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequest.setReadGrant(true);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be null")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("resourceId")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withSubjectIdNull_thenException() throws Exception {
        AclEntryRequest entryRequest = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), null);
        entryRequest.setReadGrant(true);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be null")))
                .andExpect(jsonPath("$.detailErrors[0].field", Matchers.containsString("subjectId")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withResourceTypeNotFound_thenException() throws Exception {
        AclEntryRequest entryRequestUpdate = new AclEntryRequest("no_exist_" + System.currentTimeMillis(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequestUpdate.setReadGrant(true);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenUpdateEntry_withSubjectTypeNotFound_thenException() throws Exception {
        AclEntryRequest entryRequestUpdate = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, "no_exist_" + System.currentTimeMillis(), 2L);
        entryRequestUpdate.setReadGrant(true);

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // update
        mvc.perform(put("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequestUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andDo(print());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenUpdateEntry_withNoGrant_thenDeleteEntryAndReturnEmptyEntry() throws Exception {
        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create subject type
        AclSubjectType aclSubjectType = new AclSubjectType(1L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(aclSubjectType);

        // create entry
        AclEntry entry = new AclEntry(aclResourceType.getId(), 1L, aclSubjectType.getId(), 2L, true, false, false, null, false);
        aclService.save(entry);

        // update entry
        AclEntryRequest entryRequestUpdate = new AclEntryRequest(ResourceTypeCode.COURSE.toString(), 1L, SubjectTypeCode.GROUP.toString(), 2L);
        entryRequestUpdate.setReadGrant(false);
        entryRequestUpdate.setWriteGrant(false);
        entryRequestUpdate.setDeleteGrant(false);

        mvc.perform(put("/api/acl/" + entry.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entryRequestUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.nullValue()))
                .andDo(print());

        // getById
        try {
            aclService.findById(entry.getId());
        } catch (ResourceNotFoundException ex) {
            Assert.assertEquals(ex.getResourceName(), "Entry");
            Assert.assertEquals(ex.getFieldName(), "id");
            throw ex;
        }
    }

    @Test
    public void whenDeleteEntry_withIdNotFound_then400() throws Exception {
        mvc.perform(delete("/api/acl/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("Entry not found with id")))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void whenGetAllResourceIds_withResourceTypeNotFound_then400() throws Exception {
        mvc.perform(get("/api/acl/resources/user/1?resourceType=no_exist123&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenGetAllResourceIds_withPermissionNotFound_thenReturnEmptyList() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        mvc.perform(get("/api/acl/resources/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=no_exist123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    @Test
    public void whenGetAllResourceIds_withSubjectUserTypeNotFound_then400() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        mvc.perform(get("/api/acl/resources/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andExpect(jsonPath("$.message", Matchers.containsString("USER")))
                .andDo(print());
    }

    @Test
    public void whenGetAllResourceIds_withSubjectGroupTypeNotFound_then400() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create user subject type
        AclSubjectType userSubjectType = new AclSubjectType(2L, SubjectTypeCode.USER.toString());
        aclService.saveSubjectType(userSubjectType);

        mvc.perform(get("/api/acl/resources/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andExpect(jsonPath("$.message", Matchers.containsString("GROUP")))
                .andDo(print());
    }

    @Test
    public void whenGetAllResourceIds_then200() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create user subject type
        AclSubjectType userSubjectType = new AclSubjectType(2L, SubjectTypeCode.USER.toString());
        aclService.saveSubjectType(userSubjectType);

        // create group subject type
        AclSubjectType groupSubjectType = new AclSubjectType(3L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(groupSubjectType);

        // create entry
        AclEntry entry = new AclEntry(aclResourceType.getId(), 1L, groupSubjectType.getId(), 2L, true, false, false, null, false);
        aclService.save(entry);
        AclEntry entry1 = new AclEntry(aclResourceType.getId(), 2L, groupSubjectType.getId(), 1L, true, false, false, null, true);
        aclService.save(entry1);
        AclEntry entry2 = new AclEntry(aclResourceType.getId(), 3L, groupSubjectType.getId(), 2L, false, true, false, null, false);
        aclService.save(entry2);
        AclEntry entry3 = new AclEntry(aclResourceType.getId(), 4L, groupSubjectType.getId(), 2L, false, false, true, null, false);
        aclService.save(entry3);

        mvc.perform(get("/api/acl/resources/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(4)));

        mvc.perform(get("/api/acl/resources/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.WRITE.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.equalTo(entry2.getResourceId().intValue())));

        mvc.perform(get("/api/acl/resources/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.DELETE.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.equalTo(entry3.getResourceId().intValue())));
    }

    @Test
    @Rollback(false)
    public void whenGetResourceIdByResourceId_withResourceTypeNotFound_then400() throws Exception {
        mvc.perform(get("/api/acl/resources/1/user/1?resourceType=no_exist123&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenGetResourceIdByResourceId_withPermissionNotFound_thenReturnEmptyList() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        mvc.perform(get("/api/acl/resources/1/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=no_exist123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"))
                .andDo(print());
    }

    @Test
    public void whenGetResourceIdByResourceId_withSubjectUserTypeNotFound_then400() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        mvc.perform(get("/api/acl/resources/1/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andExpect(jsonPath("$.message", Matchers.containsString("USER")))
                .andDo(print());
    }

    @Test
    public void whenGetResourceIdByResourceId_withSubjectGroupTypeNotFound_then400() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create user subject type
        AclSubjectType userSubjectType = new AclSubjectType(2L, SubjectTypeCode.USER.toString());
        aclService.saveSubjectType(userSubjectType);

        mvc.perform(get("/api/acl/resources/1/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andExpect(jsonPath("$.message", Matchers.containsString("GROUP")))
                .andDo(print());
    }

    @Test
    public void whenGetResourceIdByResourceId_then200() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create user subject type
        AclSubjectType userSubjectType = new AclSubjectType(2L, SubjectTypeCode.USER.toString());
        aclService.saveSubjectType(userSubjectType);

        // create group subject type
        AclSubjectType groupSubjectType = new AclSubjectType(3L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(groupSubjectType);

        // create entry
        AclEntry entry = new AclEntry(aclResourceType.getId(), 1L, groupSubjectType.getId(), 2L, true, false, false, null, false);
        aclService.save(entry);
        AclEntry entry1 = new AclEntry(aclResourceType.getId(), 2L, groupSubjectType.getId(), 1L, true, false, false, null, true);
        aclService.save(entry1);
        AclEntry entry2 = new AclEntry(aclResourceType.getId(), 3L, groupSubjectType.getId(), 2L, false, true, false, null, false);
        aclService.save(entry2);
        AclEntry entry3 = new AclEntry(aclResourceType.getId(), 4L, groupSubjectType.getId(), 2L, false, false, true, null, false);
        aclService.save(entry3);

        mvc.perform(get("/api/acl/resources/" + entry.getResourceId() + "/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.equalTo(entry.getResourceId().intValue())));

        mvc.perform(get("/api/acl/resources/" + entry1.getResourceId() + "/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.equalTo(entry1.getResourceId().intValue())));

        mvc.perform(get("/api/acl/resources/" + entry2.getResourceId() + "/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.WRITE.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.equalTo(entry2.getResourceId().intValue())));

        mvc.perform(get("/api/acl/resources/" + entry3.getResourceId() + "/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.DELETE.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0]", Matchers.equalTo(entry3.getResourceId().intValue())));

        mvc.perform(get("/api/acl/resources/-1/user/1?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    @Rollback(false)
    public void whenCheckPermission_withResourceTypeNotFound_then400() throws Exception {
        mvc.perform(get("/api/acl/resources/1/user/1/check?resourceType=no_exist123&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclResourceType not found with type")))
                .andDo(print());
    }

    @Test
    public void whenCheckPermission_withPermissionNotFound_thenReturnEmptyList() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        mvc.perform(get("/api/acl/resources/1/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=no_exist123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"))
                .andDo(print());
    }

    @Test
    public void whenCheckPermission_withSubjectUserTypeNotFound_then400() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        mvc.perform(get("/api/acl/resources/1/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andExpect(jsonPath("$.message", Matchers.containsString("USER")))
                .andDo(print());
    }

    @Test
    public void whenCheckPermission_withSubjectGroupTypeNotFound_then400() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create user subject type
        AclSubjectType userSubjectType = new AclSubjectType(2L, SubjectTypeCode.USER.toString());
        aclService.saveSubjectType(userSubjectType);

        mvc.perform(get("/api/acl/resources/1/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", Matchers.containsString("AclSubjectType not found with type")))
                .andExpect(jsonPath("$.message", Matchers.containsString("GROUP")))
                .andDo(print());
    }

    @Test
    public void whenCheckPermission_then200() throws Exception {

        // create resource type
        AclResourceType aclResourceType = new AclResourceType(1L, ResourceTypeCode.COURSE.toString());
        aclService.saveResourceType(aclResourceType);

        // create user subject type
        AclSubjectType userSubjectType = new AclSubjectType(2L, SubjectTypeCode.USER.toString());
        aclService.saveSubjectType(userSubjectType);

        // create group subject type
        AclSubjectType groupSubjectType = new AclSubjectType(3L, SubjectTypeCode.GROUP.toString());
        aclService.saveSubjectType(groupSubjectType);

        // create entry
        AclEntry entry = new AclEntry(aclResourceType.getId(), 1L, groupSubjectType.getId(), 2L, true, false, false, null, false);
        aclService.save(entry);
        AclEntry entry1 = new AclEntry(aclResourceType.getId(), 2L, groupSubjectType.getId(), 1L, true, false, false, null, true);
        aclService.save(entry1);
        AclEntry entry2 = new AclEntry(aclResourceType.getId(), 3L, groupSubjectType.getId(), 2L, false, true, false, null, false);
        aclService.save(entry2);
        AclEntry entry3 = new AclEntry(aclResourceType.getId(), 4L, groupSubjectType.getId(), 2L, false, false, true, null, false);
        aclService.save(entry3);

        mvc.perform(get("/api/acl/resources/" + entry.getResourceId() + "/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        mvc.perform(get("/api/acl/resources/" + entry1.getResourceId() + "/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        mvc.perform(get("/api/acl/resources/" + entry2.getResourceId() + "/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.WRITE.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        mvc.perform(get("/api/acl/resources/" + entry3.getResourceId() + "/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.DELETE.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("true"));

        mvc.perform(get("/api/acl/resources/-1/user/1/check?resourceType=" + ResourceTypeCode.COURSE.toString() + "&permission=" + AclEntryPermission.READ.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("false"));
    }
}
