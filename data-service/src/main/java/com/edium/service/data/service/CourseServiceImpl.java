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
import com.edium.library.util.Utils;
import com.edium.service.data.model.Course;
import com.edium.service.data.payload.EntryCourseGrantRequest;
import com.edium.service.data.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class CourseServiceImpl implements CourseService {

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    AclService aclService;

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public PagedResponse<Course> findByOwnerId(Long ownerId, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Course> courses = courseRepository.findAllByCreatedBy(ownerId, pageable);

        return new PagedResponse<>(courses.getContent(), courses.getNumber(),
                courses.getSize(), courses.getTotalElements(), courses.getTotalPages(), courses.isLast());
    }

    @Override
    public PagedResponse<Course> findAll(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Course> courses = courseRepository.findAll(pageable);

        return new PagedResponse<>(courses.getContent(), courses.getNumber(),
                courses.getSize(), courses.getTotalElements(), courses.getTotalPages(), courses.isLast());
    }

    @Override
    public Course saveOrUpdate(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void delete(Course course) {
        courseRepository.delete(course);
    }

    @Override
    public AclEntry setPermissionForGroup(EntryCourseGrantRequest grantRequest) {
        if (Utils.isNullOrEmpty(grantRequest.getGroupId())) {
            throw new BadRequestException("GroupId is null");
        }

        courseRepository.findById(grantRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", grantRequest.getCourseId()));

        //check group exist

        AclResourceType resourceType = aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString());
        AclSubjectType subjectType = aclService.getSubjectTypeByType(SubjectTypeCode.GROUP.toString());

        return aclService.saveOrUpdate(grantRequest.mapToAclEntry(resourceType.getId(), subjectType.getId()));
    }

    @Override
    public AclEntry setPermissionForUser(EntryCourseGrantRequest grantRequest) {
        if (Utils.isNullOrEmpty(grantRequest.getUserId())) {
            throw new BadRequestException("UserId is null");
        }

        courseRepository.findById(grantRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", grantRequest.getCourseId()));

        //check user exist

        AclResourceType resourceType = aclService.getResourceTypeByType(ResourceTypeCode.COURSE.toString());
        AclSubjectType subjectType = aclService.getSubjectTypeByType(SubjectTypeCode.USER.toString());

        return aclService.saveOrUpdate(grantRequest.mapToAclEntry(resourceType.getId(), subjectType.getId()));
    }

    @Override
    public List<Course> findPriCourseForUser(Long userId) {
        List<Long> ids = aclService.getAllResourceId(userId, ResourceTypeCode.COURSE.toString(), AclEntryPermission.READ.toString());

        return courseRepository.findAllById(ids);
    }

    @Override
    public List<Course> findWritableCourseForUser(Long userId) {
        List<Long> ids = aclService.getAllResourceId(userId, ResourceTypeCode.COURSE.toString(), AclEntryPermission.WRITE.toString());

        return courseRepository.findAllById(ids);
    }

    @Override
    public List<Course> findDeletableCourseForUser(Long userId) {
        List<Long> ids = aclService.getAllResourceId(userId, ResourceTypeCode.COURSE.toString(), AclEntryPermission.DELETE.toString());

        return courseRepository.findAllById(ids);
    }

    @Override
    public PagedResponse<Course> findPriCourseForUserNew(Long userId, AclEntryPermission entryPermission, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        GroupDTO group = aclService.getGroupOfUser(userId);

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(group.getRootPath(), group.getId());

        Page<Course> courses = new PageImpl<>(Collections.emptyList());
        switch (entryPermission) {
            case READ:
                courses = courseRepository.findCoursePermissionRead(userId, group.getId(), groupIdOfUser,
                        ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(),
                        pageable);
                break;
            case DELETE:
                courses = courseRepository.findCoursePermissionDelete(userId, group.getId(), groupIdOfUser,
                        ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(),
                        pageable);
                break;
            case WRITE:
                courses = courseRepository.findCoursePermissionWrite(userId, group.getId(), groupIdOfUser,
                        ResourceTypeCode.COURSE.toString(), SubjectTypeCode.USER.toString(), SubjectTypeCode.GROUP.toString(),
                        pageable);
                break;
        }

        return new PagedResponse<>(courses.getContent(), courses.getNumber(),
                courses.getSize(), courses.getTotalElements(), courses.getTotalPages(), courses.isLast());
    }

    @Override
    public boolean checkPermissionUser(Long courseId, Long userId, String permission) {
        return aclService.checkPermission(userId, courseId, ResourceTypeCode.COURSE.toString(), permission);
    }
}
