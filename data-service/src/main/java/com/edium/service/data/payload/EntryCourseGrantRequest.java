package com.edium.service.data.payload;

import com.edium.library.model.ResourceTypeCode;
import com.edium.library.model.share.AclEntry;
import com.edium.library.payload.EntryGrantRequest;

import javax.validation.constraints.NotBlank;

public class EntryCourseGrantRequest extends EntryGrantRequest {

    @NotBlank
    private Long courseId;

    private Long groupId;
    private Long userId;
    private Long positionId;

    public AclEntry mapToAclEntry(Long resourceTypeId, Long subjectTypeId) {
        AclEntry entry = new AclEntry();

        entry.setDeleteGrant(this.isDeleteGrant());
        entry.setInherit(this.isInherit());
        entry.setPositionId(this.getPositionId());
        entry.setReadGrant(this.isReadGrant());
        entry.setResourceId(this.getCourseId());
        entry.setResourceTypeId(resourceTypeId);
        entry.setSubjectId(this.getGroupId());
        entry.setSubjectTypeId(subjectTypeId);
        entry.setWriteGrant(this.isWriteGrant());

        return entry;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
