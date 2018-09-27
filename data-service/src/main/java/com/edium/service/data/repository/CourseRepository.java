package com.edium.service.data.repository;

import com.edium.service.data.model.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Page<Course> findAllByCreatedBy(Long ownerId, Pageable pageable);

    @Query(value = "select a from Course a where a.id in (select aeg.resourceId" +
            " from AclEntry aeg" +
            " where aeg.resourceTypeId = (select id from AclResourceType where type = :resource_course_type)" +
            " and (readGrant = 1 or writeGrant = 1 or deleteGrant = 1)" +
            " and ((aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_user_type) and aeg.subjectId = :user_id)" +
            " or (aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_group_type) and aeg.subjectId = :group_id and aeg.inherit = 0)" +
            " or (aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_group_type) and aeg.subjectId in (:group_path_id) and aeg.inherit = 1)))")
    Page<Course> findCoursePermissionRead(@Param("user_id") Long userId,
                                          @Param("group_id") Long groupId,
                                          @Param("group_path_id") List<Long> groupPathId,
                                          @Param("resource_course_type") String resourceCourseType,
                                          @Param("subject_user_type") String subjectUserType,
                                          @Param("subject_group_type") String subjectGroupType,
                                          Pageable pageable);

    @Query(value = "select a from Course a where a.id in (select aeg.resourceId" +
            " from AclEntry aeg" +
            " where aeg.resourceTypeId = (select id from AclResourceType where type = :resource_course_type)" +
            " and writeGrant = 1" +
            " and ((aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_user_type) and aeg.subjectId = :user_id)" +
            " or (aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_group_type) and aeg.subjectId = :group_id and aeg.inherit = 0)" +
            " or (aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_group_type) and aeg.subjectId in (:group_path_id) and aeg.inherit = 1)))")
    Page<Course> findCoursePermissionWrite(@Param("user_id") Long userId,
                                           @Param("group_id") Long groupId,
                                           @Param("group_path_id") List<Long> groupPathId,
                                           @Param("resource_course_type") String resourceCourseType,
                                           @Param("subject_user_type") String subjectUserType,
                                           @Param("subject_group_type") String subjectGroupType,
                                           Pageable pageable);

    @Query(value = "select a from Course a where a.id in (select aeg.resourceId" +
            " from AclEntry aeg" +
            " where aeg.resourceTypeId = (select id from AclResourceType where type = :resource_course_type)" +
            " and deleteGrant = 1" +
            " and ((aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_user_type) and aeg.subjectId = :user_id)" +
            " or (aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_group_type) and aeg.subjectId = :group_id and aeg.inherit = 0)" +
            " or (aeg.subjectTypeId = (select id from AclSubjectType where type = :subject_group_type) and aeg.subjectId in (:group_path_id) and aeg.inherit = 1)))")
    Page<Course> findCoursePermissionDelete(@Param("user_id") Long userId,
                                            @Param("group_id") Long groupId,
                                            @Param("group_path_id") List<Long> groupPathId,
                                            @Param("resource_course_type") String resourceCourseType,
                                            @Param("subject_user_type") String subjectUserType,
                                            @Param("subject_group_type") String subjectGroupType,
                                            Pageable pageable);

}
