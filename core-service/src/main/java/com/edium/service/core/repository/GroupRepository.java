package com.edium.service.core.repository;

import com.edium.service.core.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    List<Group> findAllByOrganizationId(Long organizationId);

    @Query(value = "SELECT a FROM Group a WHERE a.parentId = :groupId ORDER BY a.name")
    List<Group> getAllChildenGroups(@Param(value = "groupId") Long groupId);

    @Query(value = "SELECT a FROM Group a WHERE a.encodedRootPath LIKE CONCAT(:groupPath, '%') ORDER BY a.encodedRootPath")
    List<Group> getTreeGroupByGroupPath(@Param(value = "groupPath") String groupPath);

    @Query(value = "SELECT a FROM Group a WHERE a.organization.id = :orgId ORDER BY a.encodedRootPath")
    List<Group> getTreeGroupOfOrganization(@Param(value = "orgId") Long orgId);

    @Query(value = "SELECT a FROM Group a WHERE a.parentId IS NULL AND a.organization.id = :orgId")
    List<Group> getRootGroupOfOrganization(@Param(value = "orgId") Long orgId);

    @Query(value = "SELECT a FROM Group a WHERE a.id = (SELECT b.parentId FROM Group b WHERE b.id = :groupId)")
    Group getParentOfGroup(@Param(value = "groupId") Long groupId);

}
