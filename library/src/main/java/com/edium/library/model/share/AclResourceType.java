package com.edium.library.model.share;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
public class AclResourceType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String type;

    public AclResourceType() {
    }

    public AclResourceType(Long id, @NotBlank String type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
