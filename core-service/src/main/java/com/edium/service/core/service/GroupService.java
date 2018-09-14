package com.edium.service.core.service;

import com.edium.library.payload.PagedResponse;
import com.edium.service.core.model.Group;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {

    PagedResponse<Group> findAll(int page, int size);

    Group findById(Long groupId);

    Group save(Group group);

    Group update(Group group);

    void delete(Group group);

    void deleteById(Long groupId);

}
