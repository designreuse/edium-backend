package com.edium.library.repository.share;

import com.edium.library.model.share.AclEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AclEntryRepository extends JpaRepository<AclEntry, Long> {

    @Query(value = "select *" +
            " from acl_entry_grant aeg" +
            " where aeg.resource_type_id = :resource_type_id" +
            " and (read_grant = 1 or write_grant = 1 or delete_grant = 1)" +
            " and ((aeg.subject_type_id = :user_type_id and aeg.subject_id = :user_id)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id = :group_id and aeg.inherit = 0)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id in (:group_path_id) and aeg.inherit = 1))",
            nativeQuery = true)
    List<AclEntry> findEntryPermissionRead(@Param("resource_type_id") Long resourceTypeId,
                                           @Param("user_type_id") Long userTypeId,
                                           @Param("user_id") Long userId,
                                           @Param("group_type_id") Long groupTypeId,
                                           @Param("group_id") Long groupId,
                                           @Param("group_path_id") List<Long> groupPathId);

    @Query(value = "select *" +
            " from acl_entry_grant aeg" +
            " where aeg.resource_type_id = :resource_type_id and resource_id = :resource_id" +
            " and (read_grant = 1 or write_grant = 1 or delete_grant = 1)" +
            " and ((aeg.subject_type_id = :user_type_id and aeg.subject_id = :user_id)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id = :group_id and aeg.inherit = 0)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id in (:group_path_id) and aeg.inherit = 1))",
            nativeQuery = true)
    List<AclEntry> findEntryPermissionReadResource(@Param("resource_type_id") Long resourceTypeId,
                                       @Param("resource_id") Long resourceId,
                                       @Param("user_type_id") Long userTypeId,
                                       @Param("user_id") Long userId,
                                       @Param("group_type_id") Long groupTypeId,
                                       @Param("group_id") Long groupId,
                                       @Param("group_path_id") List<Long> groupPathId);

    @Query(value = "select *" +
            " from acl_entry_grant aeg" +
            " where aeg.resource_type_id = :resource_type_id" +
            " and write_grant = 1" +
            " and ((aeg.subject_type_id = :user_type_id and aeg.subject_id = :user_id)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id = :group_id and aeg.inherit = 0)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id in (:group_path_id) and aeg.inherit = 1))",
            nativeQuery = true)
    List<AclEntry> findEntryPermissionWrite(@Param("resource_type_id") Long resourceTypeId,
                                           @Param("user_type_id") Long userTypeId,
                                           @Param("user_id") Long userId,
                                           @Param("group_type_id") Long groupTypeId,
                                           @Param("group_id") Long groupId,
                                           @Param("group_path_id") List<Long> groupPathId);

    @Query(value = "select *" +
            " from acl_entry_grant aeg" +
            " where aeg.resource_type_id = :resource_type_id and resource_id = :resource_id" +
            " and write_grant = 1" +
            " and ((aeg.subject_type_id = :user_type_id and aeg.subject_id = :user_id)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id = :group_id and aeg.inherit = 0)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id in (:group_path_id) and aeg.inherit = 1))",
            nativeQuery = true)
    List<AclEntry> findEntryPermissionWriteResource(@Param("resource_type_id") Long resourceTypeId,
                                                @Param("resource_id") Long resourceId,
                                                @Param("user_type_id") Long userTypeId,
                                                @Param("user_id") Long userId,
                                                @Param("group_type_id") Long groupTypeId,
                                                @Param("group_id") Long groupId,
                                                @Param("group_path_id") List<Long> groupPathId);

    @Query(value = "select *" +
            " from acl_entry_grant aeg" +
            " where aeg.resource_type_id = :resource_type_id" +
            " and delete_grant = 1" +
            " and ((aeg.subject_type_id = :user_type_id and aeg.subject_id = :user_id)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id = :group_id and aeg.inherit = 0)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id in (:group_path_id) and aeg.inherit = 1))",
            nativeQuery = true)
    List<AclEntry> findEntryPermissionDelete(@Param("resource_type_id") Long resourceTypeId,
                                           @Param("user_type_id") Long userTypeId,
                                           @Param("user_id") Long userId,
                                           @Param("group_type_id") Long groupTypeId,
                                           @Param("group_id") Long groupId,
                                           @Param("group_path_id") List<Long> groupPathId);

    @Query(value = "select *" +
            " from acl_entry_grant aeg" +
            " where aeg.resource_type_id = :resource_type_id and resource_id = :resource_id" +
            " and delete_grant = 1" +
            " and ((aeg.subject_type_id = :user_type_id and aeg.subject_id = :user_id)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id = :group_id and aeg.inherit = 0)" +
            " or (aeg.subject_type_id = :group_type_id and aeg.subject_id in (:group_path_id) and aeg.inherit = 1))",
            nativeQuery = true)
    List<AclEntry> findEntryPermissionDeleteResource(@Param("resource_type_id") Long resourceTypeId,
                                                 @Param("resource_id") Long resourceId,
                                                 @Param("user_type_id") Long userTypeId,
                                                 @Param("user_id") Long userId,
                                                 @Param("group_type_id") Long groupTypeId,
                                                 @Param("group_id") Long groupId,
                                                 @Param("group_path_id") List<Long> groupPathId);

    List<AclEntry> findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(Long resoureTypeId, Long resourceId,
                                                                                 Long subjectTypeId, Long subjectId);

}
