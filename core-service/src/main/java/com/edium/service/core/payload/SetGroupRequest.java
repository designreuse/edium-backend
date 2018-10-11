package com.edium.service.core.payload;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SetGroupRequest {
    @NotNull
    private Long groupId;

    @NotEmpty
    private List<String> roles;

    public SetGroupRequest(@NotNull Long groupId, @NotEmpty List<String> roles) {
        this.groupId = groupId;
        this.roles = roles;
    }

    public SetGroupRequest() {
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
