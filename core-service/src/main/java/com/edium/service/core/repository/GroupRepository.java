package com.edium.service.core.repository;

import com.edium.service.core.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByOrganizationId(Long organizationId);

}
