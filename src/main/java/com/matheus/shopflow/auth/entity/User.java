package com.matheus.shopflow.auth.entity;

import com.matheus.shopflow.shared.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_email",
                        columnNames = "email"
                )
        }
)
public class User extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    protected User() {
        // JPA only
    }

    public User(
            String name,
            String email,
            String password,
            Role role
    ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        validate();
    }

    private void validate() {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "User name cannot be empty"
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "User email cannot be empty"
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "User password cannot be empty"
            );
        }

        if (role == null) {
            throw new IllegalArgumentException(
                    "User role cannot be null"
            );
        }
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }
}