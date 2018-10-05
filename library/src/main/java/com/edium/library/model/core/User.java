package com.edium.library.model.core;

import com.edium.library.model.base.DateAudit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = {"username"}),
        @UniqueConstraint(name = "uk_user_email", columnNames = {"email"})
})
public class User extends DateAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 8, max = 255)
    @Column(nullable = false)
    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 8, max = 255)
    @JsonIgnore
    private String password;

    @NotBlank
    @Size(max = 40)
    @Column(nullable = false)
    private String name;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean enabled = true;

    private String provider;

    @Column(nullable = false)
    private boolean isPasswordDefault = false;

    private Long organizationId;

    private Long groupId;

    @Transient
    private List<UserOrganization> userOrganizations;

    public User() {
    }

    public User(String name, String username, String email, String password) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public boolean isPasswordDefault() {
        return isPasswordDefault;
    }

    public void setPasswordDefault(boolean passwordDefault) {
        isPasswordDefault = passwordDefault;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<UserOrganization> getUserOrganizations() {
        return userOrganizations;
    }

    public void setUserOrganizations(List<UserOrganization> userOrganizations) {
        this.userOrganizations = userOrganizations;
    }
}