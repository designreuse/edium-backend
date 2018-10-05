package com.edium.library.model.core;

import com.edium.library.model.base.UserDateAudit;
import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(indexes = {
        @Index(name = "user_role_idx1", columnList = "userOrganizationId"),
        @Index(name = "user_role_idx2", columnList = "roleId"),
})
public class UserRole extends UserDateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userOrganizationId", nullable = false)
    @JsonBackReference
    @NotNull
    private UserOrganization userOrganization;

    @ManyToOne()
    @JoinColumn(name = "roleId", nullable = false)
    @NotNull
    private Role role;

    public UserRole() {
    }

    public UserRole(@NotNull UserOrganization userOrganization, @NotNull Role role) {
        this.userOrganization = userOrganization;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserOrganization getUserOrganization() {
        return userOrganization;
    }

    public void setUserOrganization(UserOrganization userOrganization) {
        this.userOrganization = userOrganization;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
