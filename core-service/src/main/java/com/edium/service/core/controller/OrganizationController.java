package com.edium.service.core.controller;

import com.edium.library.model.core.User;
import com.edium.library.payload.ApiResponse;
import com.edium.library.payload.PagedResponse;
import com.edium.library.spring.ContextAwarePolicyEnforcement;
import com.edium.library.spring.PermissionObject;
import com.edium.library.spring.PermissionType;
import com.edium.library.util.AppConstants;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.service.GroupService;
import com.edium.service.core.service.OrganizationService;
import com.edium.service.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {
    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    private ContextAwarePolicyEnforcement policyEnforcement;

    @GetMapping("")
    public PagedResponse<Organization> getAllOrganizations(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                           @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return organizationService.getAllOrganization(page, size);
    }

    @PostMapping("")
    public Organization createOrganization(@Valid @RequestBody Organization org) {
        return organizationService.save(org);
    }

    @GetMapping("/{id}")
    public Organization getOrganizationById(@PathVariable(value = "id") Long orgId) {
        Organization organization = organizationService.findById(orgId);

        policyEnforcement.checkPermission(organization, PermissionType.READ, PermissionObject.ORGANIZATION);

        return organization;
    }

    @PutMapping("/{id}")
    public Organization updateOrganization(@PathVariable(value = "id") Long orgId,
                           @Valid @RequestBody Organization orgDetails) {

        Organization org = organizationService.findById(orgId);

        policyEnforcement.checkPermission(org, PermissionType.WRITE, PermissionObject.ORGANIZATION);

        org.setName(orgDetails.getName());

        return organizationService.save(org);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteOrganization(@PathVariable(value = "id") Long orgId) {
        Organization organization = organizationService.findById(orgId);

        policyEnforcement.checkPermission(organization, PermissionType.DELETE, PermissionObject.ORGANIZATION);

        organizationService.delete(organization);

        return new ApiResponse(true, "success");
    }

    @GetMapping("/{id}/tree-group")
    public List<Group> getTreeGroup(@PathVariable(value = "id") Long orgId) {
        return groupService.getTreeGroupOfOrganization(orgId);
    }

    @GetMapping("/{id}/root-group")
    public List<Group> getRootGroup(@PathVariable(value = "id") Long orgId) {
        return groupService.getRootGroupOfOrganization(orgId);
    }

    @GetMapping("/{id}/user")
    public PagedResponse<User> getUserOfOrganization(@PathVariable(value = "id") long id,
                                                     @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                     @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return userService.findByOrganizationId(id, page, size);
    }
}
