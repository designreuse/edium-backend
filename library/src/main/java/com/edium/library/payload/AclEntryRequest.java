package com.edium.library.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AclEntryRequest extends EntryGrantRequest {
    @NotBlank
    private String resourceType;

    @NotNull
    private Long resourceId;

    @NotBlank
    private String subjectType;

    @NotNull
    private Long subjectId;

    private Long positionId;

    public AclEntryRequest(@NotBlank String resourceType, @NotNull Long resourceId, @NotBlank String subjectType, @NotNull Long subjectId) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.subjectType = subjectType;
        this.subjectId = subjectId;
    }

    public AclEntryRequest() {
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }
}
