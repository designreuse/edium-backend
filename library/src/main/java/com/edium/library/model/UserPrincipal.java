package com.edium.library.model;

import com.edium.library.model.core.User;
import com.edium.library.model.core.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserPrincipal implements UserDetails {
    private Long id;

    private String name;

    private String username;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String password;

    private boolean enable;

    private Collection<? extends GrantedAuthority> authorities;

    private Long organizationId;

    private List<String> roles;

    public UserPrincipal() {
    }

    public UserPrincipal(Long id, String name, String username, String email, String password, boolean isEnable,
                         Long organizationId,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.enable = isEnable;
        this.organizationId = organizationId;

        roles = new ArrayList<>();
        authorities.stream().forEach(o -> roles.add(((GrantedAuthority) o).getAuthority()));
    }

//    public static UserPrincipal create(User user) {
//        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        if (user.getOrganizationId() != null) {
//            for (UserOrganization userOrganization : user.getUserOrganizations()) {
//                if (userOrganization.getOrganizationId().equals(user.getOrganizationId())) {
//                    for (UserRole role : userOrganization.getRoles()) {
//                        grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole().getCode()));
//                    }
//                }
//            }
//        } else {
//            grantedAuthorities.add(new SimpleGrantedAuthority(RoleCode.ROLE_NORMAL_USER.toString()));
//        }
//        return new UserPrincipal(
//                user.getId(),
//                user.getName(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getPassword(),
//                user.isEnabled(),
//                user.getOrganizationId(),
//                grantedAuthorities
//        );
//    }

    public static UserPrincipal create(User user, List<UserRole> roles) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (user.getOrganizationId() != null) {
            for (UserRole role : roles) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getRole().getCode()));
            }
        } else {
            grantedAuthorities.add(new SimpleGrantedAuthority(RoleCode.ROLE_NORMAL_USER.toString()));
        }
        return new UserPrincipal(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                user.getOrganizationId(),
                grantedAuthorities
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enable;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enable;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
