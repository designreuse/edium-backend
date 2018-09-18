package com.edium.library.service;

import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.payload.GroupDTO;

import java.util.List;

public interface AclService {

    List<Long> getAllResourceId(Long userId, String resourceType, String permission);

    List<Long> getResourceIdByResourceId(Long userId, Long resourceId, String resourceType, String permission);

    boolean checkPermission(Long userId, Long resourceId, String resourceType, String permission);

    AclEntry findById(Long entryId);

    AclEntry save(AclEntry entry);

    AclEntry update(AclEntry entry);

    AclEntry saveOrUpdate(AclEntry entry);

    void delete(AclEntry entry);

    AclEntry findOne(Long resourceTypeId, Long resourceId, Long subjectTypeId, Long subjectid);

    // AclResourceType
    List<AclResourceType> findAllResourceType();

    AclResourceType getResourceTypeByType(String type);

    AclResourceType saveResourceType(AclResourceType resourceType);

    AclResourceType updateResourceType(AclResourceType resourceType);

    void deleteResourceTypeByType(String resourceType);

    // AclSubjectType
    List<AclSubjectType> findAllSubjectType();

    AclSubjectType getSubjectTypeByType(String type);

    AclSubjectType saveSubjectType(AclSubjectType subjectType);

    AclSubjectType updateSubjectType(AclSubjectType subjectType);

    void deleteSubjectTypeByType(String subjectType);

    abstract GroupDTO getGroupOfUser(Long userId);

}
