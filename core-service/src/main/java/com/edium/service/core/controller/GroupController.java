package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.spring.ContextAwarePolicyEnforcement;
import com.edium.service.core.model.Group;
import com.edium.service.core.model.Organization;
import com.edium.service.core.payload.GroupRequest;
import com.edium.service.core.repository.GroupRepository;
import com.edium.service.core.repository.NoteRepository;
import com.edium.service.core.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/group")
public class GroupController {
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    private ContextAwarePolicyEnforcement policy;

    @GetMapping("")
    public List<Group> getAllGroup() {
        return groupRepository.findAll();
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
            Group parent = groupRepository.findById(details.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", details.getParentId()));

            group.setGroupLevel(parent.getGroupLevel() + 1);
            group.setRootPath(parent.getRootPath() + "/" + parent.getId());
        } else {
            group.setGroupLevel(0l);
            group.setRootPath("/");
        }

        return groupRepository.save(group);
    }

    @GetMapping("/{id}")
    public Group getGroupById(@PathVariable(value = "id") Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", id));
    }

    @PutMapping("/{id}")
    public Group updateGroup(@PathVariable(value = "id") Long id,
                                           @Valid @RequestBody GroupRequest details) {

        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));

        Organization organization = organizationRepository.findById(details.getOrganizationId())
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", details.getOrganizationId()));

        group.setName(details.getName());
        group.setOrganization(organization);
        group.setParentId(details.getParentId());

        if (details.getParentId() != null) {
            Group parent = groupRepository.findById(details.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group", "id", details.getParentId()));
            group.setGroupLevel(parent.getGroupLevel() + 1);
            group.setRootPath(parent.getRootPath() + "/" + parent.getId());
        } else {
            group.setGroupLevel(0l);
            group.setRootPath("/");
        }

        return groupRepository.save(group);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable(value = "id") Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", id));

        groupRepository.delete(group);

        return ResponseEntity.ok().build();
    }
}
