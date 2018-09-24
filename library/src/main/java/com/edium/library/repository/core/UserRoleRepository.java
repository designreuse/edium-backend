package com.edium.library.repository.core;

import com.edium.library.model.core.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query(value = "SELECT a FROM UserRole a, UserOrganization b WHERE a.userOrganization.id = b.id AND b.user.id = :userId")
    List<UserRole> getByUserId(@Param(value = "userId") Long userId);

    List<UserRole> getByUserOrganization_Id(Long userOrganizationId);

}
