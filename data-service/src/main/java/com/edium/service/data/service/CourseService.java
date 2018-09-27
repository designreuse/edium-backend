package com.edium.service.data.service;

import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.share.AclEntry;
import com.edium.library.payload.PagedResponse;
import com.edium.service.data.model.Course;
import com.edium.service.data.payload.EntryCourseGrantRequest;

import java.util.List;
import java.util.Optional;

public interface CourseService {

    Optional<Course> findById(Long id);

    PagedResponse<Course> findByOwnerId(Long ownerId, int page, int size);

    PagedResponse<Course> findAll(int page, int size);

    Course saveOrUpdate(Course course);

    void delete(Course course);

    AclEntry setPermissionForGroup(EntryCourseGrantRequest grantRequest);

    AclEntry setPermissionForUser(EntryCourseGrantRequest grantRequest);

    List<Course> findPriCourseForUser(Long userId);

    List<Course> findWritableCourseForUser(Long userId);

    List<Course> findDeletableCourseForUser(Long userId);

    PagedResponse<Course> findPriCourseForUserNew(Long userId, AclEntryPermission entryPermission, int page, int size);

    boolean checkPermissionUser(Long courseId, Long userId, String permission);
}
