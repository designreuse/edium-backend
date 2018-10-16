package com.edium.service.core.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.User;
import com.edium.library.payload.GroupDTO;
import com.edium.library.service.share.BaseAclServiceImpl;
import com.edium.service.core.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AclServiceImpl extends BaseAclServiceImpl {

    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @Override
    public GroupDTO getGroupOfUser(Long userId) {
        User user = userService.getById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Group group = groupService.getCurrentGroupOfUser(user);

        return new GroupDTO(group.getId(), group.getName(), group.getParentId(), group.getGroupLevel(), group.getRootPath());
    }
}
