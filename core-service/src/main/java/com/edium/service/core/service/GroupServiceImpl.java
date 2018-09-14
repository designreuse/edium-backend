package com.edium.service.core.service;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.model.core.User;
import com.edium.library.payload.PagedResponse;
import com.edium.library.repository.core.UserRepository;
import com.edium.library.util.Utils;
import com.edium.service.core.model.Group;
import com.edium.service.core.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

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
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group update(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public void delete(Group group) {
        groupRepository.delete(group);
    }

    @Override
    public void deleteById(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        delete(group);
    }
}
