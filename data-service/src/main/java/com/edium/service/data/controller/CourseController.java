package com.edium.service.data.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.share.AclEntry;
import com.edium.library.payload.ApiResponse;
import com.edium.library.payload.PagedResponse;
import com.edium.library.util.AppConstants;
import com.edium.service.data.model.Course;
import com.edium.service.data.payload.EntryCourseGrantRequest;
import com.edium.service.data.service.CourseService;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/courses")
public class CourseController {

    @Autowired
    CourseService courseService;

    @GetMapping("")
    public PagedResponse<Course> getAllCourse(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return courseService.findAll(page, size);
    }

    @GetMapping("{id}")
    public Course getById(@PathVariable(value = "id") Long id) {
        return courseService.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteCourse(@PathVariable(value = "id") Long id) {
        Course course = courseService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        courseService.delete(course);

        return new ApiResponse(true, "SUCCESS");
    }

    @PostMapping("")
    public Course createCourse(@Valid @RequestBody Course details) {
        return courseService.saveOrUpdate(details);
    }

    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable(value = "id") Long id,
                              @Valid @RequestBody Course details) {
        return courseService.findById(id)
                .map(course -> {
                    course.setName(details.getName());
                    course.setShortDescription(details.getShortDescription());
                    return courseService.saveOrUpdate(course);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    @GetMapping("/{id}/user/{userId}/check")
    public boolean checkPermissionUser(@PathVariable(value = "id") Long courseId,
                                           @PathVariable(value = "userId") Long userId,
                                           @RequestParam(value = "permission") String permission) {

        Optional<AclEntryPermission> entryPermission = Enums.getIfPresent(AclEntryPermission.class, permission);

        if (!entryPermission.isPresent()) {
            throw new ResourceNotFoundException("Permission", "value", permission);
        }

        return courseService.checkPermissionUser(courseId, userId, entryPermission.get().toString());
    }

    @PostMapping("/permission/group/{groupId}")
    public AclEntry setPermissionForGroup(@Valid @RequestBody EntryCourseGrantRequest grantRequest,
                                          @PathVariable(name = "groupId") Long groupId) {
        grantRequest.setGroupId(groupId);
        return courseService.setPermissionForGroup(grantRequest);
    }

    @PostMapping("/permission/user/{userId}")
    public AclEntry setPermissionForUser(@Valid @RequestBody EntryCourseGrantRequest grantRequest,
                                     @PathVariable(name = "userId") Long userId) {
        grantRequest.setUserId(userId);
        return courseService.setPermissionForUser(grantRequest);
    }

    @GetMapping("/user/{userId}/owner")
    public PagedResponse<Course> getAllCourse(@PathVariable(value = "userId") Long ownerId,
                                              @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return courseService.findByOwnerId(ownerId, page, size);
    }

    @GetMapping("/user/{userId}/read")
    public PagedResponse<Course> getAllReadableCourse(@PathVariable(name = "userId") Long userId,
                                             @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                             @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return courseService.findPriCourseForUserNew(userId, AclEntryPermission.READ, page, size);
    }

    @GetMapping("/user/{userId}/write")
    public PagedResponse<Course> getAllWriteableCourse(@PathVariable(name = "userId") Long userId,
                                              @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                              @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return courseService.findPriCourseForUserNew(userId, AclEntryPermission.WRITE, page, size);
    }

    @GetMapping("/user/{userId}/delete")
    public PagedResponse<Course> getAllDeleteableCourse(@PathVariable(name = "userId") Long userId,
                                               @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                               @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return courseService.findPriCourseForUserNew(userId, AclEntryPermission.DELETE, page, size);
    }

}
