package com.edium.library.repository.core;

import com.edium.library.model.core.UserOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganizationRepository extends JpaRepository<UserOrganization, Long> {

    UserOrganization getByUser_IdAndOrganizationId(Long userId, Long organizationId);

}
