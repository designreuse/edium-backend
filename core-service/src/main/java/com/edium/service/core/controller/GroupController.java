package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.payload.PagedResponse;
import com.edium.library.spring.ContextAwarePolicyEnforcement;
import com.edium.library.util.AppConstants;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.payload.GroupRequest;
import com.edium.service.core.repository.OrganizationRepository;
import com.edium.service.core.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    @Autowired
    GroupService groupService;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    private ContextAwarePolicyEnforcement policy;

    @GetMapping("")
    public PagedResponse<Group> getAllGroup(@RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return groupService.findAll(page, size);
    }

    @PostMapping("")
    public Group createGroup(@Valid @RequestBody GroupRequest details) {
        Group group = new Group();

        Organization organization = organizationRepository.findById(details.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", details.getOrganizationId()));

        group.setName(details.getName());
        group.setOrganization(organization);
        group.setParentId(details.getParentId());

        if (details.getParentId() != null) {
            Group parent = groupService.findById(details.getParentId());

            group.setGroupLevel(parent.getGroupLevel() + 1);
            group.setRootPath(parent.getRootPath() + "/" + parent.getId());
        } else {
            group.setGroupLevel(0l);
            group.setRootPath("/");
        }

        return groupService.save(group);
    }

    @GetMapping("/{id}")
    public Group getGroupById(@PathVariable(value = "id") Long id) {
        return groupService.findById(id);
    }

    @PutMapping("/{id}")
    public Group updateGroup(@PathVariable(value = "id") Long id,
                                           @Valid @RequestBody GroupRequest details) {

        Group group = groupService.findById(id);

        Organization organization = organizationRepository.findById(details.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", details.getOrganizationId()));

        group.setName(details.getName());
        group.setOrganization(organization);
        group.setParentId(details.getParentId());

        if (details.getParentId() != null) {
            Group parent = groupService.findById(details.getParentId());

            group.setGroupLevel(parent.getGroupLevel() + 1);
            group.setRootPath(parent.getRootPath() + "/" + parent.getId());
        } else {
            group.setGroupLevel(0l);
            group.setRootPath("/");
        }

        return groupService.update(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable(value = "id") Long id) {
        groupService.deleteById(id);

        return ResponseEntity.ok().build();
    }
}
