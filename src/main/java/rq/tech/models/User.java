package rq.tech.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Column(nullable = false)
    private String password;

    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "Role is required")
    @Column(nullable = false)
    private String role; // ADMIN, STAFF, TECHNICIAN

    @Builder.Default
    private boolean isActive = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    // Rate Limiting Fields
    @Builder.Default
    private int failedLoginAttempts = 0;

    private LocalDateTime lockoutExpiry;

    // Password Reset Fields
    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isStaff() {
        return "STAFF".equalsIgnoreCase(role);
    }

    public boolean isTechnician() {
        return "TECHNICIAN".equalsIgnoreCase(role);
    }

    public boolean isLockedOut() {
        return lockoutExpiry != null && lockoutExpiry.isAfter(LocalDateTime.now());
    }
}
