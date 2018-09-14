package com.edium.service.core.controller;

import com.edium.library.payload.AclEntryRequest;
import com.edium.library.model.share.AclEntry;
import com.edium.library.model.share.AclResourceType;
import com.edium.library.model.share.AclSubjectType;
import com.edium.library.service.AclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("api/acl")
public class AclController {
    @Autowired
    AclService aclService;

    @RequestMapping("/resources/user/{userId}")
    public List<Long> getAllResourceIds(@PathVariable(name = "userId") Long userId,
                                        @RequestParam(name = "resourceType") String resoureType,
                                        @RequestParam(name = "permission") String permission) {
        return aclService.getAllResourceId(userId, resoureType, permission);
    }

    @PostMapping("")
    public AclEntry createEntry(@Valid @RequestBody AclEntryRequest entryRequest) {
        AclResourceType resourceType = aclService.getResourceTypeByType(entryRequest.getResourceType());

        AclSubjectType subjectType = aclService.getSubjectTypeByType(entryRequest.getSubjectType());

        AclEntry aclEntry = new AclEntry();
        aclEntry.entryRequestToEntry(entryRequest, resourceType, subjectType);

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
    public ResponseEntity<?> deleteEntry(@PathVariable(name = "id") Long entryId) {
        AclEntry aclEntry = aclService.findById(entryId);

        aclService.delete(aclEntry);
        return ResponseEntity.ok().build();
    }
}
