package com.example.iwork.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String username;
    private String email;
    private String confirmationCode;
    private String password;
    private String fullName;
    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;
    private String company;
    // Связь с локацией
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    // Оставляем строковое поле для удобства при выдаче респонса
    @Column(name = "location_string")
    private String locationString;
    private String phone;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String withUsSince;
    public String getUsername1() {
        return this.username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public String getUsername() {
        return this.getUsername1();
    }
}