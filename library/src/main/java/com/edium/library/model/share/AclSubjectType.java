package com.edium.library.model.share;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class AclSubjectType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String type;

    public AclSubjectType(Long id, @NotNull String type) {
        this.id = id;
        this.type = type;
    }

    public AclSubjectType() {
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
