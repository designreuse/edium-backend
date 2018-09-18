package com.edium.library.payload;

public class GroupDTO {
    private Long id;
    private String name;
    private Long parentId;
    private Long groupLevel;
    private String rootPath;

    public GroupDTO() {
    }

    public GroupDTO(Long id, String name, Long parentId, Long groupLevel, String rootPath) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.groupLevel = groupLevel;
        this.rootPath = rootPath;
    }

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
}
