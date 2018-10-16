package com.edium.service.data.controller;

import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.UserPrincipal;
import com.edium.library.spring.OAuthHelper;
import com.edium.service.data.DataServiceApplication;
import com.edium.service.data.config.UnitTestConfig;
import com.edium.service.data.model.Course;
import com.edium.service.data.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {DataServiceApplication.class, UnitTestConfig.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CourseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CourseService courseService;

    @Qualifier("serializingObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OAuthHelper oAuthHelper;

    private final UserPrincipal ADMIN = new UserPrincipal(2L, "", "", "", "",
            true, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN")));

    @Test
    public void whenCheckPermissionUser_thenStatus200() throws Exception {
        long courseId = 1, userId = 1;
        String permission = AclEntryPermission.READ.toString();

        Mockito.when(courseService.checkPermissionUser(courseId, userId, permission)).thenReturn(true);

        mvc.perform(get("/api/courses/"+ courseId + "/user/"+ userId + "/check?permission=" + permission)
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void givenCourse_whenUpdateCourseWithNameBlank_thenStatus400() throws Exception {
        // update
        Course courseNew = new Course(null, "test");
        mvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(courseNew)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void givenCourse_whenUpdateCourseWithShortDesBlank_thenStatus400() throws Exception {
        // update
        Course courseNew = new Course("test", null);
        mvc.perform(put("/api/courses/1")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(courseNew)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    public void givenCourse_whenCreateCourseWithNameBlank_thenStatus400() throws Exception {
        long timestamp = System.currentTimeMillis();
        Course course = new Course(null, "test" + timestamp);

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andDo(print());
    }

    @Test
    public void whenCreateCourseWithShortDesBlank_thenStatus400() throws Exception {
        long timestamp = System.currentTimeMillis();
        Course course = new Course("test" + timestamp, null);

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN))
                .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.detailErrors[0].message", Matchers.containsString("not be blank")))
                .andDo(print());
    }

    @Test
    public void whenCheckPermissionUserWithPermissionNotValid_thenStatus400() throws Exception {
        mvc.perform(get("/api/courses/1/user/1/check?permission=none")
                .contentType(MediaType.APPLICATION_JSON).with(oAuthHelper.bearerToken(ADMIN)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", containsString("not found with")))
                .andDo(print());
    }

}
