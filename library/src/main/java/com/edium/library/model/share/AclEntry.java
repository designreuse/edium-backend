package com.edium.library.model.share;

import com.edium.library.payload.AclEntryRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity()
@Table(name = "acl_entry_grant", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"resourceTypeId", "resourceId", "subjectTypeId", "subjectId"}, name = "acl_entry_resource_subject_uk")
})
public class AclEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long resourceTypeId;

    @NotNull
    private Long resourceId;

    @NotNull
    private Long subjectTypeId;

    @NotNull
    private Long subjectId;

    @NotNull
    private boolean readGrant = true;

    @NotNull
    private boolean writeGrant = true;

    @NotNull
    private boolean deleteGrant = true;

    private Long positionId;

    @NotNull
    private boolean inherit = true;

    public void entryRequestToEntry(AclEntryRequest entryRequest, AclResourceType resourceType,
                                    AclSubjectType subjectType) {
        this.setDeleteGrant(entryRequest.isDeleteGrant());
        this.setInherit(entryRequest.isInherit());
        this.setPositionId(entryRequest.getPositionId());
        this.setReadGrant(entryRequest.isReadGrant());
        this.setResourceId(entryRequest.getResourceId());
        this.setResourceTypeId(resourceType.getId());
        this.setSubjectId(entryRequest.getSubjectId());
        this.setSubjectTypeId(subjectType.getId());
        this.setWriteGrant(entryRequest.isWriteGrant());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceTypeId() {
        return resourceTypeId;
    }

    public void setResourceTypeId(Long resourceTypeId) {
        this.resourceTypeId = resourceTypeId;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getSubjectTypeId() {
        return subjectTypeId;
    }

    public void setSubjectTypeId(Long subjectTypeId) {
        this.subjectTypeId = subjectTypeId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isReadGrant() {
        return readGrant;
    }

    public void setReadGrant(boolean readGrant) {
        this.readGrant = readGrant;
    }

    public boolean isWriteGrant() {
        return writeGrant;
    }

    public void setWriteGrant(boolean writeGrant) {
        this.writeGrant = writeGrant;
    }

    public boolean isDeleteGrant() {
        return deleteGrant;
    }

    public void setDeleteGrant(boolean deleteGrant) {
        this.deleteGrant = deleteGrant;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }
}
