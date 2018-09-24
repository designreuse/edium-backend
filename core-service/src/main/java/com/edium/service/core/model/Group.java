package com.edium.service.core.model;

import com.edium.library.model.UserDateAudit;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "team_group", indexes = {
        @Index(name = "group_idx1", columnList = "organizationId"),
        @Index(name = "group_idx2", columnList = "parentId"),
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_group_name", columnNames = "name"),
        @UniqueConstraint(name = "uk_group_encode_id", columnNames = "encodedId"),
        @UniqueConstraint(name = "uk_group_encode_path", columnNames = "encodedRootPath"),
        @UniqueConstraint(name = "uk_group_root_path", columnNames = "rootPath"),
})
public class Group extends UserDateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private Long parentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organizationId")
    private Organization organization;

    @NotNull
    @Column(nullable = false)
    private Long groupLevel = 0l;

    @Column(nullable = true, length = 1000)
    private String rootPath;

    @Column(nullable = true)
    private String encodedId;

    @Column(nullable = true, length = 1000)
    private String encodedRootPath;

    @Transient
    private String parentPath;

    @Transient
    private String parentEncodedPath;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private List<Menu> menus;
//
//    @JsonBackReference
//    @ManyToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "parent_id")
//    private Menu parent;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Long getGroupLevel() {
        return groupLevel;
    }

    public void setGroupLevel(Long groupLevel) {
        this.groupLevel = groupLevel;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getEncodedId() {
        return encodedId;
    }

    public void setEncodedId(String encodedId) {
        this.encodedId = encodedId;
    }

    public String getEncodedRootPath() {
        return encodedRootPath;
    }

    public void setEncodedRootPath(String encodedRootPath) {
        this.encodedRootPath = encodedRootPath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getParentEncodedPath() {
        return parentEncodedPath;
    }

    public void setParentEncodedPath(String parentEncodedPath) {
        this.parentEncodedPath = parentEncodedPath;
    }
}
