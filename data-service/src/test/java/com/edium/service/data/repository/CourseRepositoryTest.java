package com.edium.service.data.repository;

import com.edium.library.model.ResourceTypeCode;
import com.edium.library.model.SubjectTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.service.data.model.Course;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.NONE)
public class CourseRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository courseRepository;

    private Course course, course1, course2;

    @Before
    public void setUp() {
        course = new Course("course_test", "course_test");
        course1 = new Course("course_test1", "course_test1");
        course2 = new Course("course_test2", "course_test2");
        entityManager.persist(course);
        entityManager.persist(course1);
        entityManager.persist(course2);

        entityManager.persist(new AclSubjectType(1l, SubjectTypeCode.GROUP.toString()));
        entityManager.persist(new AclSubjectType(2l, SubjectTypeCode.USER.toString()));

        entityManager.persist(new AclResourceType(1l, ResourceTypeCode.COURSE.toString()));
        entityManager.persist(new AclResourceType(2l, ResourceTypeCode.USER.toString()));

        entityManager.persist(new AclEntry(1l, course.getId(), 1l,
                1l, true, false, false, null, false));
        entityManager.persist(new AclEntry(1l, course1.getId(), 1l,
                1l, false, true, false, null, false));
        entityManager.persist(new AclEntry(1l, course2.getId(), 1l,
                1l, false, false, true, null, false));

        entityManager.flush();
    }

    @Test
    public void whenFindCoursePermissionRead_thenReturnCourses() throws SQLException {

        // when
        Pageable pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "name"));

        Page<Course> courses = courseRepository.findCoursePermissionRead(1l, 1l, Arrays.asList(1l),
                ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(), pageable);

        // then
        Assert.assertEquals(courses.getTotalElements(), 3);

        List<String> courseNames = new ArrayList<>();
        courses.getContent().forEach(course3 -> courseNames.add(course3.getName()));

        Assert.assertThat(courseNames, CoreMatchers.hasItem(course.getName()));
    }

    @Test
    public void whenFindCoursePermissionWrite_thenReturnCourses() throws SQLException {
        // when
        Pageable pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "name"));

        Page<Course> courses = courseRepository.findCoursePermissionWrite(1l, 1l, Arrays.asList(1l),
                ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(), pageable);

        // then
        Assert.assertEquals(courses.getTotalElements(), 1);

        List<String> courseNames = new ArrayList<>();
        courses.getContent().forEach(course3 -> courseNames.add(course3.getName()));

        Assert.assertThat(courseNames, CoreMatchers.hasItem(course1.getName()));
    }

    @Test
    public void whenFindCoursePermissionDelete_thenReturnCourses() throws SQLException {
        // when
        Pageable pageable = PageRequest.of(0, 10, new Sort(Sort.Direction.ASC, "name"));

        Page<Course> courses = courseRepository.findCoursePermissionDelete(1l, 1l, Arrays.asList(1l),
                ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(), pageable);

        // then
        Assert.assertEquals(courses.getTotalElements(), 1);

        List<String> courseNames = new ArrayList<>();
        courses.getContent().forEach(course3 -> courseNames.add(course3.getName()));

        Assert.assertThat(courseNames, CoreMatchers.hasItem(course2.getName()));
    }
}
