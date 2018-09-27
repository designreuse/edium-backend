package com.edium.service.core.service;

import com.edium.library.payload.GroupDTO;
import com.edium.library.service.BaseAclServiceImpl;
import com.edium.service.core.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AclServiceImpl extends BaseAclServiceImpl {

    @Autowired
    GroupService groupService;

    @Override
    public GroupDTO getGroupOfUser(Long userId) {
        Group group = groupService.getCurrentGroupOfUser(userId);

        return new GroupDTO(group.getId(), group.getName(), group.getParentId(), group.getGroupLevel(), group.getRootPath());
    }
}
