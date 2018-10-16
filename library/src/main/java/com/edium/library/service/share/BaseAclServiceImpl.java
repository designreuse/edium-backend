package com.edium.library.service.share;

import com.edium.library.exception.ResourceExistException;
import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.AclEntryPermission;
import com.edium.library.model.SubjectTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.payload.GroupDTO;
import com.edium.library.repository.share.AclEntryRepository;
import com.edium.library.repository.share.AclResourceTypeRepository;
import com.edium.library.repository.share.AclSubjectTypeRepository;
import com.edium.library.util.Utils;
import com.google.common.base.Enums;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class BaseAclServiceImpl implements AclService {

    @Autowired
    AclEntryRepository aclEntryRepository;

    @Autowired
    AclResourceTypeRepository aclResourceTypeRepository;

    @Autowired
    AclSubjectTypeRepository aclSubjectTypeRepository;

    @Override
    public List<Long> getAllResourceId(Long userId, String resourceType, String permission) {
        AclResourceType aclResourceType = this.getResourceTypeByType(resourceType);

        Optional<AclEntryPermission> entryPermission = Enums.getIfPresent(AclEntryPermission.class, permission).toJavaUtil();
        if (!entryPermission.isPresent()) {
            return Collections.emptyList();
        }

        AclSubjectType userType = this.getSubjectTypeByType(SubjectTypeCode.USER.toString());
        AclSubjectType groupType = this.getSubjectTypeByType(SubjectTypeCode.GROUP.toString());

        GroupDTO group = getGroupOfUser(userId);

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(group.getRootPath(), group.getId());

        List<AclEntry> entries = null;
        switch (entryPermission.get()) {
            case READ:
                entries = aclEntryRepository.findEntryPermissionRead(aclResourceType.getId(), userType.getId(), userId,
                        groupType.getId(), group.getId(), groupIdOfUser);
                break;
            case WRITE:
                entries = aclEntryRepository.findEntryPermissionWrite(aclResourceType.getId(), userType.getId(), userId,
                        groupType.getId(), group.getId(), groupIdOfUser);
                break;
            case DELETE:
                entries = aclEntryRepository.findEntryPermissionDelete(aclResourceType.getId(), userType.getId(), userId,
                        groupType.getId(), group.getId(), groupIdOfUser);
                break;
        }

        if (entries != null && !entries.isEmpty()) {
            List<Long> ids = new ArrayList<>();

            entries.forEach(aclEntry -> ids.add(aclEntry.getResourceId()));

            return ids;
        }

        return Collections.emptyList();
    }

    @Override
    public List<Long> getResourceIdByResourceId(Long userId, Long resourceId, String resourceType, String permission) {
        AclResourceType aclResourceType = this.getResourceTypeByType(resourceType);

        Optional<AclEntryPermission> entryPermission = Enums.getIfPresent(AclEntryPermission.class, permission).toJavaUtil();
        if (!entryPermission.isPresent()) {
            return Collections.emptyList();
        }

        AclSubjectType userType = this.getSubjectTypeByType(SubjectTypeCode.USER.toString());
        AclSubjectType groupType = this.getSubjectTypeByType(SubjectTypeCode.GROUP.toString());

        GroupDTO group = getGroupOfUser(userId);

        List<Long> groupIdOfUser = Utils.getGroupPathOfGroup(group.getRootPath(), group.getId());

        List<AclEntry> entries = null;
        switch (entryPermission.get()) {
            case READ:
                entries = aclEntryRepository.findEntryPermissionReadResource(aclResourceType.getId(), resourceId, userType.getId(), userId,
                        groupType.getId(), group.getId(), groupIdOfUser);
                break;
            case WRITE:
                entries = aclEntryRepository.findEntryPermissionWriteResource(aclResourceType.getId(), resourceId, userType.getId(), userId,
                        groupType.getId(), group.getId(), groupIdOfUser);
                break;
            case DELETE:
                entries = aclEntryRepository.findEntryPermissionDeleteResource(aclResourceType.getId(), resourceId, userType.getId(), userId,
                        groupType.getId(), group.getId(), groupIdOfUser);
                break;
        }

        if (entries != null && !entries.isEmpty()) {
            List<Long> ids = new ArrayList<>();

            entries.forEach(aclEntry -> ids.add(aclEntry.getResourceId()));

            return ids;
        }

        return Collections.emptyList();
    }

    @Override
    public boolean checkPermission(Long userId, Long resourceId, String resourceType, String permission) {
        List<Long> ids = getResourceIdByResourceId(userId, resourceId, resourceType, permission);
        return ids != null && !ids.isEmpty();
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
    public AclEntry saveOrUpdate(AclEntry entry) {
        AclEntry existEntry = findOne(entry.getResourceTypeId(), entry.getResourceId(), entry.getSubjectTypeId(), entry.getSubjectId());

        if (entry.isReadGrant() || entry.isWriteGrant() || entry.isDeleteGrant()) {
            // Entry already grant: existed --> update, otherwise insert
            if (existEntry != null) {
                // existed --> update
                existEntry.setWriteGrant(entry.isWriteGrant());
                existEntry.setReadGrant(entry.isReadGrant());
                existEntry.setPositionId(entry.getPositionId());
                existEntry.setInherit(entry.isInherit());
                existEntry.setDeleteGrant(entry.isDeleteGrant());

                return aclEntryRepository.save(existEntry);
            } else {
                // insert
                return aclEntryRepository.save(entry);
            }
        } else {
            // Entry no grant: existed --> delete, otherwise no thing
            if (existEntry != null) {
                // entry existed --> delete
                aclEntryRepository.delete(existEntry);
                return existEntry;
            }
        }
        return new AclEntry();
    }

    @Override
    public void delete(AclEntry entry) {
        aclEntryRepository.delete(entry);
    }

    @Override
    public AclEntry findOne(Long resourceTypeId, Long resourceId, Long subjectTypeId, Long subjectId) {
        List<AclEntry> aclEntries = aclEntryRepository.findByResourceTypeIdAndResourceIdAndSubjectTypeIdAndSubjectId(resourceTypeId, resourceId,
                subjectTypeId, subjectId);

        if (aclEntries != null && !aclEntries.isEmpty()) {
            return aclEntries.get(0);
        }

        return null;
    }

    // AclResourceType methods
    @Override
    public List<AclResourceType> findAllResourceType() {
        return aclResourceTypeRepository.findAll();
    }

    @Override
    @Cacheable(value = "data-resource-type-cache", key = "#type")
    public AclResourceType getResourceTypeByType(String type) {
        System.out.println("getResourceTypeByType: " + type);
        return aclResourceTypeRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("AclResourceType", "type", type));
    }

    @Override
    @CachePut(value = "data-resource-type-cache", key = "#resourceType.type")
    public AclResourceType saveResourceType(AclResourceType resourceType) {
        Optional<AclResourceType> aclResourceType = aclResourceTypeRepository.findByType(resourceType.getType());

        if (aclResourceType.isPresent()) {
            throw new ResourceExistException("AclResourceType", "type", resourceType.getType());
        }

        return aclResourceTypeRepository.save(resourceType);
    }

    @Override
    @CachePut(value = "data-resource-type-cache", key = "#resourceType.type")
    public AclResourceType updateResourceType(AclResourceType resourceType) {
        AclResourceType aclResourceType = aclResourceTypeRepository.findByType(resourceType.getType())
                .orElseThrow(() -> new ResourceNotFoundException("AclResourceType", "type", resourceType.getType()));

        aclResourceType.setId(resourceType.getId());

        return aclResourceTypeRepository.save(aclResourceType);
    }

    @Override
    @CacheEvict(value = "data-resource-type-cache", key = "#resourceType")
    public void deleteResourceTypeByType(String resourceType) {
        AclResourceType aclResourceType = aclResourceTypeRepository.findByType(resourceType)
                .orElseThrow(() -> new ResourceNotFoundException("AclResourceType", "type", resourceType));

        aclResourceTypeRepository.delete(aclResourceType);
    }

    // AclSubjectType methods
    @Override
    public List<AclSubjectType> findAllSubjectType() {
        return aclSubjectTypeRepository.findAll();
    }

    @Override
    @Cacheable(value = "data-subject-type-cache", key = "#type")
    public AclSubjectType getSubjectTypeByType(String type) {
        System.out.println("getSubjectTypeByType: " + type);
        return aclSubjectTypeRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("AclSubjectType", "type", type));
    }

    @Override
    @CachePut(value = "data-subject-type-cache", key = "#subjectType.type")
    public AclSubjectType saveSubjectType(AclSubjectType subjectType) {
        Optional<AclSubjectType> aclSubjectType = aclSubjectTypeRepository.findByType(subjectType.getType());

        if (aclSubjectType.isPresent()) {
            throw new ResourceExistException("AclSubjectType", "type", subjectType.getType());
        }

        return aclSubjectTypeRepository.save(subjectType);
    }

    @Override
    @CachePut(value = "data-subject-type-cache", key = "#subjectType.type")
    public AclSubjectType updateSubjectType(AclSubjectType subjectType) {
        AclSubjectType aclSubjectType = aclSubjectTypeRepository.findByType(subjectType.getType())
                .orElseThrow(() -> new ResourceNotFoundException("AclSubjectType", "type", subjectType.getType()));

        aclSubjectType.setId(subjectType.getId());

        return aclSubjectTypeRepository.save(aclSubjectType);
    }

    @Override
    @CacheEvict(value = "data-subject-type-cache", key = "#subjectType")
    public void deleteSubjectTypeByType(String subjectType) {
        AclSubjectType aclSubjectType = aclSubjectTypeRepository.findByType(subjectType)
                .orElseThrow(() -> new ResourceNotFoundException("AclSubjectType", "type", subjectType));

        aclSubjectTypeRepository.delete(aclSubjectType);
    }

}
