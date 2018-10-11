package com.edium.service.core.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.User;
import com.edium.library.model.core.UserOrganization;
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
    UserService userService;

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

        checkParent(group);

        Group newGroup = groupRepository.save(group);

        BaseX base62 = new BaseX();
        String encode = base62.encode(BigInteger.valueOf(newGroup.getId()));

        newGroup.setEncodedId(base62.encode(BigInteger.valueOf(encode.length())) + encode);
        newGroup.setParentPath(group.getParentPath());
        newGroup.setParentEncodedPath(group.getParentEncodedPath());

        setPath(newGroup);

        return groupRepository.save(newGroup);
    }

    private void checkParent(Group group) {
        if (group.getParentId() != null) {
            Group parent = findById(group.getParentId());

            group.setGroupLevel(parent.getGroupLevel() + 1);
            group.setParentPath(parent.getRootPath());
            group.setParentEncodedPath(parent.getEncodedRootPath());
        }
    }

    @Override
    public Group update(Group group) {
        checkParent(group);

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
    public void deleteById(Long groupId) {
        groupRepository.delete(findById(groupId));
    }

    @Override
    public List<Group> getAllChildrenGroups(Long groupId) {
        return groupRepository.getAllChildrenGroups(groupId);
    }

    @Override
    public List<Group> getTreeGroupByGroupId(Long groupId) {

        Group group = findById(groupId);

        return groupRepository.getTreeGroupByGroupPath(group.getEncodedRootPath());
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
        UserOrganization userOrganization = userOrganizationRepository.getByUser_IdAndOrganizationId(userId, organizationId);
        return userOrganization == null ? null : userOrganization.getGroupId();
    }

    @Override
    public PagedResponse<Group> getGroupsOfUser(Long userId, int page, int size) {
        Utils.validatePageNumberAndSize(page, size);

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Group> groups = groupRepository.getGroupsOfUser(userId, pageable);

        return new PagedResponse<>(groups.getContent(), groups.getNumber(),
                groups.getSize(), groups.getTotalElements(), groups.getTotalPages(), groups.isLast());
    }

    @Override
    public Group getCurrentGroupOfUser(Long userId) {
        User user = userService.getById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return findById(user.getGroupId());
    }
}
