package com.edium.service.data.service;

import com.edium.library.exception.BadRequestException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.ResourceTypeCode;
import com.edium.library.model.SubjectTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.payload.GroupDTO;
import com.edium.library.payload.PagedResponse;
import com.edium.library.service.AclService;
import com.edium.library.util.AppConstants;
import com.edium.library.util.Utils;
import com.edium.service.data.model.Course;
import com.edium.service.data.payload.EntryCourseGrantRequest;
import com.edium.service.data.repository.CourseRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class CourseServiceImplTest {

    @TestConfiguration
    static class CourseServiceImplTestContextConfiguration {

        @Bean
        public CourseService courseService() {
            return new CourseServiceImpl();
        }
    }

    @Autowired
    private CourseService courseService;

    @MockBean
    private CourseRepository courseRepository;

    @MockBean
    private AclService aclService;

    @Test
    public void whenFindById_thenReturnCourse() {
        // setup
        Course course = new Course("test", "test");
        course.setId(50L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        // when
        Optional<Course> found = courseService.findById(course.getId());

        // then
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals(found.get().getName(), course.getName());
    }

    @Test
    public void whenFindById_thenReturnEmpty() {
        // setup
        Course course = new Course("test", "test");
        course.setId(1L);

        Mockito.when(courseRepository.findById(course.getId())).thenReturn(Optional.empty());

        // when
        Optional<Course> found = courseService.findById(course.getId());

        // then
        Assert.assertFalse(found.isPresent());
    }

    @Test
    public void whenFindByOwnerId_thenReturnPage() {
        // setup
        Course course = new Course("test", "test");
        course.setCreatedBy(1L);

        int page = Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER), size = Integer.parseInt(AppConstants.DEFAULT_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Mockito.when(courseRepository.findAllByCreatedBy(course.getCreatedBy(), pageable)).thenReturn(new PageImpl<>(Collections.singletonList(course)));

        // when
        PagedResponse<Course> result = courseService.findByOwnerId(course.getCreatedBy(), page, size);

        // then
        Assert.assertEquals(result.getTotalElements(), 1);
        Assert.assertEquals(result.getContent().get(0).getName(), course.getName());
        Assert.assertEquals(result.getTotalPages(), 1);
    }

    @Test
    public void whenFindByOwnerId_thenReturnEmpty() {
        // setup
        Course course = new Course("test", "test");
        course.setCreatedBy(1L);

        int page = Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER), size = Integer.parseInt(AppConstants.DEFAULT_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Mockito.when(courseRepository.findAllByCreatedBy(course.getCreatedBy(), pageable)).thenReturn(Page.empty());

        // when
        PagedResponse<Course> result = courseService.findByOwnerId(course.getCreatedBy(), page, size);

        // then
        Assert.assertEquals(result.getTotalElements(), 0);
        Assert.assertEquals(result.getTotalPages(), 1);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindByOwnerIdSizeNegative_thenReturnPage() {
        // setup
        Course course = new Course("test", "test");
        course.setCreatedBy(1L);

        int page = 0, size = -1;

        // when
         courseService.findByOwnerId(course.getCreatedBy(), page, size);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindByOwnerIdPageAndSizeNegative_thenException() {
        // setup
        Course course = new Course("test", "test");
        course.setCreatedBy(1L);

        int page = -1, size = 10;

        // when
        courseService.findByOwnerId(course.getCreatedBy(), page, size);
    }

    @Test
    public void whenFindAll_thenReturnPage() {
        // setup
        Course course = new Course("test", "test");
        course.setCreatedBy(1L);

        int page = Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER), size = Integer.parseInt(AppConstants.DEFAULT_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Mockito.when(courseRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(course)));

        // when
        PagedResponse<Course> result = courseService.findAll(page, size);

        // then
        Assert.assertEquals(result.getTotalElements(), 1);
        Assert.assertEquals(result.getContent().get(0).getName(), course.getName());
        Assert.assertEquals(result.getTotalPages(), 1);
    }

    @Test
    public void whenFindAll_thenReturnEmpty() {
        // setup
        int page = Integer.parseInt(AppConstants.DEFAULT_PAGE_NUMBER), size = Integer.parseInt(AppConstants.DEFAULT_PAGE_SIZE);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Mockito.when(courseRepository.findAll(pageable)).thenReturn(Page.empty());

        // when
        PagedResponse<Course> result = courseService.findAll(page, size);

        // then
        Assert.assertEquals(result.getTotalElements(), 0);
        Assert.assertEquals(result.getTotalPages(), 1);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindAllSizeNegative_thenReturnPage() {
        int page = 0, size = -1;

        // when
        courseService.findAll(page, size);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindAllPageAndSizeNegative_thenException() {
        int page = -1, size = 10;

        // when
        courseService.findAll(page, size);
    }

    private EntryCourseGrantRequest setupGrantRequest() {
        EntryCourseGrantRequest grantRequest = new EntryCourseGrantRequest();

        grantRequest.setCourseId(1L);
        grantRequest.setGroupId(1L);
        grantRequest.setUserId(1L);

        return grantRequest;
    }

    @Test(expected = BadRequestException.class)
    public void whenSetPermissionForGroupWithGroupIdNull_thenException() {
        // when
        courseService.setPermissionForGroup(new EntryCourseGrantRequest());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetPermissionForGroupWithCourseNotFound_thenException() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenThrow(new ResourceNotFoundException("Course", "id", grantRequest.getCourseId()));

        // when
        courseService.setPermissionForGroup(grantRequest);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetPermissionForGroupWithAclResourceTypeNotFound_thenException() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenReturn(Optional.of(new Course("test", "test")));
        Mockito.when(aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString())).thenThrow(new ResourceNotFoundException("AclResourceType", "type", "test"));

        // when
        courseService.setPermissionForGroup(grantRequest);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetPermissionForGroupWithAclSubjectTypeNotFound_thenException() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenReturn(Optional.of(new Course("test", "test")));
        Mockito.when(aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString())).thenReturn(new AclResourceType());
        Mockito.when(aclService.getSubjectTypeByType(SubjectTypeCode.GROUP.toString())).thenThrow(new ResourceNotFoundException("AclResourceType", "type", "test"));

        // when
        courseService.setPermissionForGroup(grantRequest);
    }

    @Test()
    public void whenSetPermissionForGroup_thenReturnEntry() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        AclEntry entry = grantRequest.mapToAclEntry(1L, 1L);

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenReturn(Optional.of(new Course("test", "test")));
        Mockito.when(aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString())).thenReturn(new AclResourceType(1L, "test"));
        Mockito.when(aclService.getSubjectTypeByType(SubjectTypeCode.GROUP.toString())).thenReturn(new AclSubjectType(1L, "test"));
        Mockito.when(aclService.saveOrUpdate(entry)).thenReturn(entry);

        // when
        AclEntry result = courseService.setPermissionForGroup(grantRequest);

        // then
        Assert.assertEquals(result, entry);
    }

    @Test(expected = BadRequestException.class)
    public void whenSetPermissionForUserWithUserIdNull_thenException() {
        // when
        courseService.setPermissionForUser(new EntryCourseGrantRequest());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetPermissionForUserWithCourseNotFound_thenException() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenThrow(new ResourceNotFoundException("Course", "id", grantRequest.getCourseId()));

        // when
        courseService.setPermissionForUser(grantRequest);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetPermissionForUserWithAclResourceTypeNotFound_thenException() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenReturn(Optional.of(new Course("test", "test")));
        Mockito.when(aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString())).thenThrow(new ResourceNotFoundException("AclResourceType", "type", "test"));

        // when
        courseService.setPermissionForUser(grantRequest);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void whenSetPermissionForUserWithAclSubjectTypeNotFound_thenException() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenReturn(Optional.of(new Course("test", "test")));
        Mockito.when(aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString())).thenReturn(new AclResourceType());
        Mockito.when(aclService.getSubjectTypeByType(SubjectTypeCode.USER.toString())).thenThrow(new ResourceNotFoundException("AclResourceType", "type", "test"));

        // when
        courseService.setPermissionForUser(grantRequest);
    }

    @Test()
    public void whenSetPermissionForUser_thenReturnEntry() {
        // setup
        EntryCourseGrantRequest grantRequest = setupGrantRequest();

        AclEntry entry = grantRequest.mapToAclEntry(1L, 1L);

        Mockito.when(courseRepository.findById(grantRequest.getCourseId())).thenReturn(Optional.of(new Course("test", "test")));
        Mockito.when(aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString())).thenReturn(new AclResourceType(1L, "test"));
        Mockito.when(aclService.getSubjectTypeByType(SubjectTypeCode.USER.toString())).thenReturn(new AclSubjectType(1L, "test"));
        Mockito.when(aclService.saveOrUpdate(entry)).thenReturn(entry);

        // when
        AclEntry result = courseService.setPermissionForUser(grantRequest);

        // then
        Assert.assertEquals(result, entry);
    }

    @Test
    public void whenFindPriCourseForUser_thenReturnCourses() {
        // setup
        long userId = 1;
        List<Long> ids = Collections.singletonList(1L);
        List<Course> courses = Collections.singletonList(new Course());

        Mockito.when(aclService.getAllResourceId(userId, ResourceTypeCode.COURSE.toString(), AclEntryPermission.READ.toString())).thenReturn(ids);
        Mockito.when(courseRepository.findAllById(ids)).thenReturn(courses);

        // when
        List<Course> result = courseService.findPriCourseForUser(userId);

        // then
        Assert.assertEquals(result, courses);
    }

    @Test
    public void whenFindWritableCourseForUser_thenReturnCourses() {
        // setup
        long userId = 1;
        List<Long> ids = Collections.singletonList(1L);
        List<Course> courses = Collections.singletonList(new Course());

        Mockito.when(aclService.getAllResourceId(userId, ResourceTypeCode.COURSE.toString(), AclEntryPermission.WRITE.toString())).thenReturn(ids);
        Mockito.when(courseRepository.findAllById(ids)).thenReturn(courses);

        // when
        List<Course> result = courseService.findWritableCourseForUser(userId);

        // then
        Assert.assertEquals(result, courses);
    }

    @Test
    public void whenFindDeletableCourseForUser_thenReturnCourses() {
        // setup
        long userId = 1;
        List<Long> ids = Collections.singletonList(1L);
        List<Course> courses = Collections.singletonList(new Course());

        Mockito.when(aclService.getAllResourceId(userId, ResourceTypeCode.COURSE.toString(), AclEntryPermission.DELETE.toString())).thenReturn(ids);
        Mockito.when(courseRepository.findAllById(ids)).thenReturn(courses);

        // when
        List<Course> result = courseService.findDeletableCourseForUser(userId);

        // then
        Assert.assertEquals(result, courses);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindPriCourseForUserNewWithSizeNegative_thenException() {
        // setup
        int page = 0, size = -1;

        // when
        courseService.findPriCourseForUserNew(1L, AclEntryPermission.READ, page, size);
    }

    @Test(expected = BadRequestException.class)
    public void whenFindPriCourseForUserNewWithPageNegative_thenException() {
        // setup
        int page = -1, size = 10;

        // when
        courseService.findPriCourseForUserNew(1L, AclEntryPermission.READ, page, size);
    }

    @Test(expected = RestClientException.class)
    public void whenFindPriCourseForUserNewWithRestClientFail_thenException() {
        // setup
        int page = 0, size = 10;
        long userId = 1;
        Mockito.when(aclService.getGroupOfUser(userId)).thenThrow(new RestClientException("test"));

        // when
        courseService.findPriCourseForUserNew(userId, AclEntryPermission.READ, page, size);
    }

    @Test(expected = NullPointerException.class)
    public void whenFindPriCourseForUserNewWithGroupNotFound_thenException() {
        // setup
        int page = 0, size = 10;
        long userId = 1;
        Mockito.when(aclService.getGroupOfUser(userId)).thenReturn(null);

        // when
        courseService.findPriCourseForUserNew(userId, AclEntryPermission.READ, page, size);
    }

    @Test
    public void whenFindPriCourseForUserNew_thenReturnCourses() {
        // setup
        int page = 0, size = 10;
        long userId = 1;

        GroupDTO groupDTO = new GroupDTO(1L, "test", null, 0L, "/1");
        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(groupDTO.getRootPath(), groupDTO.getId());
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Mockito.when(aclService.getGroupOfUser(userId)).thenReturn(groupDTO);
        Mockito.when(courseRepository.findCoursePermissionRead(userId, groupDTO.getId(), groupIdOfUser,
                ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(),
                pageable)).thenReturn(new PageImpl<>(Collections.singletonList(new Course())));

        // when
        PagedResponse<Course> response = courseService.findPriCourseForUserNew(userId, AclEntryPermission.READ, page, size);

        // then
        Assert.assertEquals(response.getTotalElements(), 1);
        Assert.assertEquals(response.getContent().size(), 1);
    }
}
