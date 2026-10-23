package rq.tech.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "repair_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    @Builder.Default
    private String uuid = UUID.randomUUID().toString();

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name too long (max 100)")
    @Column(nullable = false)
    private String customerName;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^(09\\d{9}|09\\d{2}-\\d{3}-\\d{4})$", message = "Invalid phone number format. Use: 09XXXXXXXXX")
    @Size(max = 20, message = "Contact number too long (max 20)")
    @Column(nullable = false)
    private String contactNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email too long (max 100)")
    private String customerEmail;

    @Size(max = 500, message = "Address too long (max 500)")
    private String address;

    @NotBlank(message = "Device is required")
    @Size(max = 100, message = "Device name too long (max 100)")
    @Column(nullable = false)
    private String device;

    @Size(max = 100, message = "Serial number too long (max 100)")
    private String serialNumber;

    @Size(max = 255, message = "Image path too long (max 255)")
    private String imagePath;

    @NotBlank(message = "Issue/Problem is required")
    @Size(max = 1000, message = "Issue description too long (max 1000)")
    @Column(nullable = false)
    private String issue;

    @Builder.Default
    private String status = "PENDING";

    @Builder.Default
    @DecimalMin(value = "0.0", message = "Bill amount cannot be negative")
    @DecimalMax(value = "1000000.0", message = "Bill amount exceeds limit (max 1M)")
    @Column(precision = 10, scale = 2)
    private BigDecimal bill = BigDecimal.ZERO;

    @Builder.Default
    private String paymentStatus = "UNPAID";

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime dateReceived;

    private LocalDateTime dateCompleted;

    @Lob
    private String notes;

    public boolean isPending() {
        return "PENDING".equalsIgnoreCase(status);
    }

    public boolean isOngoing() {
        return "ONGOING".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isPaid() {
        return "PAID".equalsIgnoreCase(paymentStatus);
    }

    public void markAsCompleted() {
        this.status = "COMPLETED";
        this.dateCompleted = LocalDateTime.now();
    }

    public void markAsPaid() {
        this.paymentStatus = "PAID";
    }
}
