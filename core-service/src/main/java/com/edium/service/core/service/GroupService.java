package com.edium.service.core.service;

import com.edium.library.payload.PagedResponse;
import com.edium.service.core.model.Group;

import java.util.List;

public interface GroupService {

    PagedResponse<Group> findAll(int page, int size);

    Group findById(Long groupId);

    Group save(Group group) throws Exception;

    Group update(Group group);

    void delete(Group group);

    void deleteById(Long groupId);

    List<Group> getAllChildenGroups(Long groupId);

    List<Group> getTreeGroupByGroupId(Long groupId);

    List<Group> getTreeGroupByGroupPath(String groupPath);

    List<Group> getTreeGroupOfOrganization(Long orgId);

    List<Group> getRootGroupOfOrganization(Long orgId);

    Group getParentOfGroup(Long groupId);

    Long getGroupOfUserInOrganization(Long userId, Long organizationId);

    PagedResponse<Group> getGroupsOfUser(Long userId, int page, int size);

    Group getCurrentGroupOfUser(Long userId);

}
