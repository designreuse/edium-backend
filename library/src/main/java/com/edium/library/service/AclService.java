package com.edium.library.service;

import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;

import java.util.List;

public interface AclService {

    List<Long> getAllResourceId(Long userId, String resourceType, String permission);

    List<Long> getResourceIdByResourceId(Long userId, Long resourceId, String resourceType, String permission);

    boolean checkPermission(Long userId, Long resourceId, String resourceType, String permission);

    AclEntry findById(Long entryId);

    AclEntry save(AclEntry entry);

    AclEntry update(AclEntry entry);

    void delete(AclEntry entry);

    AclResourceType getResourceTypeByType(String type);

    AclSubjectType getSubjectTypeByType(String type);

}
