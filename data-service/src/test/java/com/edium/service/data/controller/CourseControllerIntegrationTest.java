package com.edium.service.data.controller;

import com.edium.library.model.UserPrincipal;
import com.edium.library.payload.PagedResponse;
import com.edium.library.spring.OAuthHelper;
import com.edium.service.data.DataServiceApplication;
import com.edium.service.data.model.Course;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = DataServiceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OAuthHelper oAuthHelper;

    private final UserPrincipal ADMIN = new UserPrincipal(2L, "", "", "", "",
            true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @Test
    public void givenCourse_whenCRUD_thenStatus200()
            throws Exception {
        // setup
        long timestamp = System.currentTimeMillis();
        Course course = new Course("test" + timestamp, "test" + timestamp);

        // Create
        MvcResult response = mvc.perform(post("/api/courses/")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(course.getName()))).andReturn();
        Course courseInsert = objectMapper.readValue(response.getResponse().getContentAsString(), Course.class);
        course.setId(courseInsert.getId());

        // getById
        mvc.perform(get("/api/courses/" + course.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(course.getName())));

        // getAll
        response = mvc.perform(get("/api/courses?page=0&size=" + Integer.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        PagedResponse<Course> courses = objectMapper.readValue(response.getResponse().getContentAsString(), new TypeReference<PagedResponse<Course>>(){});
        Assert.assertTrue(courses.getContent().stream().anyMatch(course1 -> course1.getName().equals(course.getName())));

        // update
        timestamp = System.currentTimeMillis();
        Course courseNew = new Course("test" + timestamp, "test" + timestamp);
        mvc.perform(put("/api/courses/" + course.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(courseNew)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(courseNew.getName())));

        // delete
        mvc.perform(delete("/api/courses/" + course.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success", is(true)));

        // getById
        mvc.perform(get("/api/courses/" + course.getId())
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void givenCourse_whenGetByIdWithIdNotFound_thenStatus400()
            throws Exception {
        mvc.perform(get("/api/courses/0")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void givenCourse_whenDeleteCourseWithIdNotFound_thenStatus400()
            throws Exception {
        mvc.perform(get("/api/courses/0")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    @Rollback(false)
    public void givenCourse_whenUpdateCourseWithIdNotFound_thenStatus400()
            throws Exception {
        long timestamp = System.currentTimeMillis();
        Course courseNew = new Course("test" + timestamp, "test" + timestamp);

        mvc.perform(put("/api/courses/0")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(courseNew)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

}
