package com.edium.auth.model;

import com.edium.auth.validators.UniqueUsername;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Entity
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @UniqueUsername(message="Username already exists")
    @Size(min = 8, max = 255, message = "Username have to be grater than 8 characters")
    @Column(unique = true)
    private String username;

    @NotNull
    @Size(min = 8, max = 255, message = "Password have to be grater than 8 characters")
    private String password;

    @NotNull
    private boolean enabled = true;

    @NotNull
    private boolean credentialsExpired = false;

    @NotNull
    private boolean expired = false;

    @NotNull
    private boolean locked = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "AccountRole", joinColumns = @JoinColumn(name = "accountId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"))
    private Set<Role> roles;

    public Account() {

    }

    public Account(Account account){
        this.id = account.getId();
        this.username = account.getUsername();
        this.password = account.getPassword();
        this.enabled = account.isEnabled();
        this.credentialsExpired = account.isCredentialsExpired();
        this.locked = account.isExpired();
        this.roles = account.getRoles();
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

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        for (Role role : getRoles()) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getCode()));
        }
        return grantedAuthorities;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCredentialsExpired() {
        return credentialsExpired;
    }

    public void setCredentialsExpired(boolean credentialsExpired) {
        this.credentialsExpired = credentialsExpired;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
