package com.iitj.projectplatform;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

@Entity
@Table(name = "users")
public class User implements UserDetails {
    private static final long serialVersionID = 1L;
    @Column(name = "username")
    @Id
    private String username;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;

    @Column(name = "account_non_locked")
    private Boolean accountNonLocked;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;





    public User() {
    }

    public User(String username) {
        this.username = username;
    }

    public User(String username, String name, String email, String password, Boolean accountNonLocked, Role role) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
        this.accountNonLocked = accountNonLocked;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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



    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }


    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(final Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()->"read");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", accountNonLocked=" + accountNonLocked +
                '}';
    }


}
