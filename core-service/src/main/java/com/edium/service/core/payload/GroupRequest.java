package com.edium.service.core.payload;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class GroupRequest {
    @NotBlank
    private String name;

    @NotNull
    private Long organizationId;

    private Long parentId;

    public GroupRequest() {
    }

    public GroupRequest(@NotBlank String name, @NotNull Long organizationId, Long parentId) {
        this.name = name;
        this.organizationId = organizationId;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
