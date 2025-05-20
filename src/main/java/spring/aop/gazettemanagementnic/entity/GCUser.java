package spring.aop.gazettemanagementnic.entity;


import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "gcuser")
public class GCUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Size(min = 10, max = 15, message = "Username must be between 10 and 15 characters")
    @Pattern(regexp = "^(?=.{10,15}$)(?=[^_]*_[^_]*$).*$", message = "Username must contain exactly one underscore")
    private String username;


    @Column(nullable = false, unique = true)
    private String password; // stored as BCrypt-hashed string

    @Column(nullable = false)
    @Size(min = 5, max = 9, message = "Role  must be between 5 to 9 characters")
    private String role;


    @Column(nullable = false)
    private LocalDate date;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
