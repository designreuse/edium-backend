package com.edium.service.data.service;

import com.edium.library.payload.PagedResponse;
import com.edium.service.data.model.Course;
import com.edium.service.data.payload.EntryCourseGrantRequest;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Optional<Course> findById(Long id);

    PagedResponse<Course> findByOwnerId(Long ownerId, int page, int size);

    PagedResponse<Course> findAll(int page, int size);

    List<Course> findByGroupId(Long groupId);

    Course saveOrUpdate(Course course);

    void delete(Course course);

    void deleteById(Long id);

    void setPermissionForGroup(EntryCourseGrantRequest grantRequest);

    void setPermissionForUser(EntryCourseGrantRequest grantRequest);

    List<Course> findPriCourseForUser(Long userId);

    List<Course> findWriteableCourseForUser(Long userId);

    List<Course> findDeleteableCourseForUser(Long userId);

    boolean checkPermissionUser(Long courseId, Long userId, String permission);
}
