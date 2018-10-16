package com.edium.library.controller;

import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.payload.AclEntryRequest;
import com.edium.library.payload.ApiResponse;
import com.edium.library.service.share.AclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/acl")
public class AclController {
    @Autowired
    AclService aclService;

    // AclEntry methods
    @GetMapping("/{id}")
    public AclEntry getEntryById(@PathVariable(name = "id") Long entryId) {
        return aclService.findById(entryId);
    }

    @PostMapping("")
    public AclEntry createEntry(@Valid @RequestBody AclEntryRequest entryRequest) {
        AclEntry aclEntry = new AclEntry();
        aclEntry.entryRequestToEntry(entryRequest, aclService.getResourceTypeByType(entryRequest.getResourceType()),
                aclService.getSubjectTypeByType(entryRequest.getSubjectType()));

        return aclService.save(aclEntry);
    }

    @PutMapping("/{id}")
    public AclEntry updateEntry(@PathVariable(name = "id") Long entryId,
                                @Valid @RequestBody AclEntryRequest entryRequest) {
        AclResourceType resourceType = aclService.getResourceTypeByType(entryRequest.getResourceType());

        AclSubjectType subjectType = aclService.getSubjectTypeByType(entryRequest.getSubjectType());

        AclEntry aclEntry = aclService.findById(entryId);
        aclEntry.entryRequestToEntry(entryRequest, resourceType, subjectType);

        return aclService.update(aclEntry);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteEntry(@PathVariable(name = "id") Long entryId) {
        AclEntry aclEntry = aclService.findById(entryId);

        aclService.delete(aclEntry);
        return new ApiResponse(true, "success");
    }

    // AclResourceType methods
    @GetMapping("/resourceType")
    public List<AclResourceType> getResourceTypeByType() {
        return aclService.findAllResourceType();
    }

    @GetMapping("/resourceType/{type}")
    public AclResourceType getResourceTypeByType(@PathVariable(name = "type") String type) {
        return aclService.getResourceTypeByType(type);
    }

    @PostMapping("/resourceType")
    public AclResourceType createResourceType(@Valid @RequestBody AclResourceType aclResourceType) {
        return aclService.saveResourceType(aclResourceType);
    }

    @PutMapping("/resourceType")
    public AclResourceType updateResourceType(@Valid @RequestBody AclResourceType aclResourceType) {
        return aclService.updateResourceType(aclResourceType);
    }

    @DeleteMapping("/resourceType/{type}")
    public ApiResponse deleteResourceType(@PathVariable(name = "type") String type) {
        aclService.deleteResourceTypeByType(type);
        return new ApiResponse(true, "success");
    }

    // AclSubjectType methods
    @GetMapping("/subjectType")
    public List<AclSubjectType> getSubjectTypeByType() {
        return aclService.findAllSubjectType();
    }

    @GetMapping("/subjectType/{type}")
    public AclSubjectType getSubjectTypeByType(@PathVariable(name = "type") String type) {
        return aclService.getSubjectTypeByType(type);
    }

    @PostMapping("/subjectType")
    public AclSubjectType createSubjectType(@Valid @RequestBody AclSubjectType aclSubjectType) {
        return aclService.saveSubjectType(aclSubjectType);
    }

    @PutMapping("/subjectType")
    public AclSubjectType updateSubjectType(@Valid @RequestBody AclSubjectType aclSubjectType) {
        return aclService.updateSubjectType(aclSubjectType);
    }

    @DeleteMapping("/subjectType/{type}")
    public ApiResponse deleteSubjectType(@PathVariable(name = "type") String type) {
        aclService.deleteSubjectTypeByType(type);
        return new ApiResponse(true, "success");
    }

    @RequestMapping("/resources/user/{userId}")
    public List<Long> getAllResourceIds(@PathVariable(name = "userId") Long userId,
                                        @RequestParam(name = "resourceType") String resourceType,
                                        @RequestParam(name = "permission") String permission) {
        return aclService.getAllResourceId(userId, resourceType, permission);
    }

    @RequestMapping("/resources/{resourceId}/user/{userId}")
    public List<Long> getResourceIdByResourceId(@PathVariable(name = "resourceId") Long resourceId,
                                                @PathVariable(name = "userId") Long userId,
                                                @RequestParam(name = "resourceType") String resourceType,
                                                @RequestParam(name = "permission") String permission) {
        return aclService.getResourceIdByResourceId(userId, resourceId, resourceType, permission);
    }

    @RequestMapping("/resources/{resourceId}/user/{userId}/check")
    public boolean checkPermission(@PathVariable(name = "resourceId") Long resourceId,
                                                @PathVariable(name = "userId") Long userId,
                                                @RequestParam(name = "resourceType") String resourceType,
                                                @RequestParam(name = "permission") String permission) {
        return aclService.checkPermission(userId, resourceId, resourceType, permission);
    }
}
