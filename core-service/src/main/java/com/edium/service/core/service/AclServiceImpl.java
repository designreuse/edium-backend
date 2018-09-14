package com.edium.service.core.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.SubjectTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.service.AclService;
import com.edium.library.util.Utils;
import com.edium.service.core.model.Group;
import com.edium.library.repository.share.AclEntryRepository;
import com.edium.library.repository.share.AclResourceTypeRepository;
import com.edium.library.repository.share.AclSubjectTypeRepository;
import com.google.common.base.Enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AclServiceImpl implements AclService {
    @Autowired
    AclEntryRepository aclEntryRepository;

    @Autowired
    AclSubjectTypeRepository aclSubjectTypeRepository;

    @Autowired
    AclResourceTypeRepository aclResourceTypeRepository;

    @Autowired
    GroupService groupService;

    @Override
    public List<Long> getAllResourceId(Long userId, String resourceType, String permission) {
        Optional<AclResourceType> aclResourceType = aclResourceTypeRepository.findByType(resourceType);

        if (!aclResourceType.isPresent()) {
            return Collections.emptyList();
        }

        AclEntryPermission entryPermission = Enums.getIfPresent(AclEntryPermission.class, permission).get();
        if (entryPermission == null) {
            return Collections.emptyList();
        }

        List<AclSubjectType> subjectTypes = aclSubjectTypeRepository.findAll();
        Long userTypeId = null, groupTypeId = null;
        if (subjectTypes != null && !subjectTypes.isEmpty()) {
            for (AclSubjectType type : subjectTypes) {
                if (type.getType().equals(SubjectTypeCode.USER)) {
                    userTypeId = type.getId();
                } else if (type.getType().equals(SubjectTypeCode.GROUP)) {
                    groupTypeId = type.getId();
                }
            }
        }

        if (userTypeId == null || groupTypeId == null) {
            return Collections.emptyList();
        }

        Group group = groupService.getGroupOfUser(userId);

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(group.getRootPath());

        return aclEntryRepository.findEntryPermissionRead(aclResourceType.get().getId(), userTypeId, userId,
                groupTypeId, group.getId(), groupIdOfUser);
    }

    @Override
    public AclEntry findById(Long entryId) {
        return aclEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Entry", "id", entryId));
    }

    @Override
    public AclEntry save(AclEntry entry) {
        if (entry.isReadGrant() || entry.isWriteGrant() || entry.isDeleteGrant()) {
            return aclEntryRepository.save(entry);
        }
        return new AclEntry();
    }

    @Override
    public AclEntry update(AclEntry entry) {
        if (entry.isReadGrant() || entry.isWriteGrant() || entry.isDeleteGrant()) {
            return aclEntryRepository.save(entry);
        } else {
            aclEntryRepository.delete(entry);
            return new AclEntry();
        }
    }

    @Override
    public void delete(AclEntry entry) {
        aclEntryRepository.delete(entry);
    }

    @Override
    public AclResourceType getResourceTypeByType(String type) {
        return aclResourceTypeRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("ResourceType", "type", type));
    }

    @Override
    public AclSubjectType getSubjectTypeByType(String type) {
        return aclSubjectTypeRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("SubjectType", "type", type));
    }
}
