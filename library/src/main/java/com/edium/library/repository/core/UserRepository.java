package com.edium.library.repository.core;

import com.edium.library.model.core.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT a FROM User a where a.id in (SELECT b.user.id FROM UserOrganization b WHERE b.organizationId = :organizationId)")
    Page<User> findByOrganizationId(@Param(value = "organizationId") Long organizationId, Pageable pageable);

}
