package com.edium.library.model.share;

import com.edium.library.payload.AclEntryRequest;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

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

    public AclEntry() {
    }

    public AclEntry(@NotNull Long resourceTypeId, @NotNull Long resourceId, @NotNull Long subjectTypeId,
                    @NotNull Long subjectId, @NotNull boolean readGrant, @NotNull boolean writeGrant,
                    @NotNull boolean deleteGrant, Long positionId, @NotNull boolean inherit) {
        this.resourceTypeId = resourceTypeId;
        this.resourceId = resourceId;
        this.subjectTypeId = subjectTypeId;
        this.subjectId = subjectId;
        this.readGrant = readGrant;
        this.writeGrant = writeGrant;
        this.deleteGrant = deleteGrant;
        this.positionId = positionId;
        this.inherit = inherit;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AclEntry entry = (AclEntry) o;
        return readGrant == entry.readGrant &&
                writeGrant == entry.writeGrant &&
                deleteGrant == entry.deleteGrant &&
                inherit == entry.inherit &&
                Objects.equals(id, entry.id) &&
                Objects.equals(resourceTypeId, entry.resourceTypeId) &&
                Objects.equals(resourceId, entry.resourceId) &&
                Objects.equals(subjectTypeId, entry.subjectTypeId) &&
                Objects.equals(subjectId, entry.subjectId) &&
                Objects.equals(positionId, entry.positionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, resourceTypeId, resourceId, subjectTypeId, subjectId, readGrant, writeGrant, deleteGrant, positionId, inherit);
    }
}
