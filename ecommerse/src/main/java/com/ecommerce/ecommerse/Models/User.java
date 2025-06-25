package com.ecommerce.ecommerse.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = { // Changed table name from user_details
        @UniqueConstraint(columnNames = "username"), // Assuming username should be unique
        @UniqueConstraint(columnNames = "email")      // Assuming email should be unique
})
public class User {

    @Id
    private String id;
    private String name;

    @Column(nullable = false)
    private String username; // Often used for login, ensure it's consistently used or derived (e.g. from email)

    @Column(nullable = false)
    private String password;

    private int phoneNumber; // Consider String for phone numbers to keep leading zeros and special chars

    @Column(nullable = false)
    private String email;

    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    // Constructor updated to include username and password, adjust as necessary
    public User(String id, String name, String username, String password, int phoneNumber, String email, String address) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password; // Remember to encode this before saving if set directly
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username; // Ensure this aligns with UserDetailsService (e.g., if email is used as username)
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

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
