package com.edium.library.repository.core;

import com.edium.library.model.core.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {

    List<UserOrganization> getByUser_Id(Long userId);

    UserOrganization getByUser_IdAndOrganizationId(Long userId, Long organizationId);

}
