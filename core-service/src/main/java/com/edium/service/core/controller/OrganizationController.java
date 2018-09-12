package com.edium.service.core.controller;

import com.edium.library.exception.ResourceNotFoundException;
import com.edium.library.spring.ContextAwarePolicyEnforcement;
import com.edium.service.core.model.Note;
import com.edium.service.core.model.Organization;
import com.edium.service.core.repository.NoteRepository;
import com.edium.service.core.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/organization")
public class OrganizationController {
    @Autowired
    OrganizationRepository organizationRepository;

    @Autowired
    NoteRepository noteRepository;

    @Autowired
    private ContextAwarePolicyEnforcement policy;

    @GetMapping("")
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @PostMapping("")
    public Organization createOrganization(@Valid @RequestBody Organization org) {
        return organizationRepository.save(org);
    }

    @PreAuthorize("hasPermission(#orgId, T(com.edium.service.core.security.PermissionObject).ORGANIZATION.toString(), T(com.edium.service.core.security.PermissionType).READ)")
    @GetMapping("/{id}")
    public Organization getOrganizationById(@PathVariable(value = "id") Long orgId) {
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));
    }

    //@PreAuthorize("hasPermission(#orgId, T(com.edium.service.core.security.PermissionObject).ORGANIZATION.toString(), T(com.edium.service.core.security.PermissionType).READ)")
    @GetMapping("/{id}/notes")
    public List<Note> getNotesByOrganization(@PathVariable(value = "id") Long orgId) {
        Organization org = new Organization();
        org.setId(orgId);
        policy.checkPermission(org, "READ");
        return noteRepository.findAllByOrganizationId(orgId);
    }

    @PreAuthorize("hasPermission(#orgDetails, T(com.edium.service.core.security.PermissionType).WRITE + '_' + T(com.edium.service.core.security.PermissionObject).ORGANIZATION)")
    @PutMapping("/{id}")
    public Organization updateOrganization(@PathVariable(value = "id") Long orgId,
                           @Valid @RequestBody Organization orgDetails) {

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        org.setName(orgDetails.getName());

        return organizationRepository.save(org);
    }

    @PreAuthorize("hasPermission(#orgId, T(com.edium.service.core.security.PermissionObject).ORGANIZATION.toString(), T(com.edium.service.core.security.PermissionType).READ)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable(value = "id") Long orgId) {
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", "id", orgId));

        organizationRepository.delete(org);

        return ResponseEntity.ok().build();
    }
}
