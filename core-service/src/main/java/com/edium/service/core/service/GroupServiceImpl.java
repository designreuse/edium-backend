package com.edium.service.core.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.UserOrganizationRepository;
import com.edium.library.util.BaseX;
import com.edium.library.util.Utils;
import com.edium.service.core.model.Group;
import com.edium.service.core.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserOrganizationRepository userOrganizationRepository;

    @Override
    public PagedResponse<Group> findAll(int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Group> groups = groupRepository.findAll(pageable);

        return new PagedResponse<>(groups.getContent(), groups.getNumber(),
                groups.getSize(), groups.getTotalElements(), groups.getTotalPages(), groups.isLast());
    }

    @Override
    public Group findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Group save(Group group) {
        Group newGroup = groupRepository.save(group);

        BaseX base62 = new BaseX();
        String encode = base62.encode(BigInteger.valueOf(newGroup.getId()));

        newGroup.setEncodedId(base62.encode(BigInteger.valueOf(encode.length())) + encode);
        newGroup.setParentPath(group.getParentPath());
        newGroup.setParentEncodedPath(group.getParentEncodedPath());

        setPath(newGroup);

        return groupRepository.save(newGroup);
    }

    @Override
    public Group update(Group group) {
        setPath(group);

        return groupRepository.save(group);
    }

    private void setPath(Group group) {
        if (group.getParentId() == null) {
            group.setRootPath("/" + group.getId());
            group.setEncodedRootPath(group.getEncodedId());
        } else {
            group.setEncodedRootPath(group.getParentEncodedPath() + "-" + group.getEncodedId());
            group.setRootPath(group.getParentPath() + "/" + group.getId());
        }
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public void deleteById(Long groupId) {
        delete(findById(groupId));
    }

    @Override
    public List<Group> getAllChildenGroups(Long groupId) {
        return groupRepository.getAllChildenGroups(groupId);
    }

    @Override
    public List<Group> getTreeGroupByGroupId(Long groupId) {

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        return groupRepository.getTreeGroupByGroupPath(group.getEncodedRootPath());
    }

    @Override
    public List<Group> getTreeGroupByGroupPath(String groupPath) {
        return groupRepository.getTreeGroupByGroupPath(groupPath);
    }

    @Override
    public List<Group> getTreeGroupOfOrganization(Long orgId) {
        return groupRepository.getTreeGroupOfOrganization(orgId);
    }

    @Override
    public List<Group> getRootGroupOfOrganization(Long orgId) {
        return groupRepository.getRootGroupOfOrganization(orgId);
    }

    @Override
    public Group getParentOfGroup(Long groupId) {
        return groupRepository.getParentOfGroup(groupId);
    }

    @Override
    public Long getGroupOfUserInOrganization(Long userId, Long organizationId) {
        return userOrganizationRepository.getByUser_IdAndOrganizationId(userId, organizationId).getGroupId();
    }
}
