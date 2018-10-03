package com.edium.library.model.core;

import com.edium.library.model.base.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_organization", columnNames = {"userId", "organizationId"})
}, indexes = {
        @Index(name = "user_organization_idx1", columnList = "userId"),
        @Index(name = "user_organization_idx2", columnList = "organizationId"),
        @Index(name = "user_organization_idx3", columnList = "groupId"),
})
public class UserOrganization extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

    @NotNull
    @Column(nullable = false)
    private Long organizationId;

    @NotNull
    @Column(nullable = false)
    private Long groupId;

    @Transient
    List<UserRole> roles;

    public UserOrganization() {
    }

    public UserOrganization(@NotNull User user, @NotNull Long organizationId, @NotNull Long groupId) {
        this.user = user;
        this.organizationId = organizationId;
        this.groupId = groupId;
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }
}
