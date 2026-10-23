package rq.tech.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name too long (max 100)")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^(09\\d{9}|09\\d{2}-\\d{3}-\\d{4})$", message = "Invalid phone number format. Use: 09XXXXXXXXX")
    @Size(max = 20, message = "Contact number too long (max 20)")
    @Column(nullable = false, unique = true)
    private String contactNumber;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email too long (max 100)")
    @Column(unique = true)
    private String email;

    @Size(max = 500, message = "Address too long (max 500)")
    private String address;

    @Builder.Default
    @Min(value = 0)
    private int totalVisits = 0;

    private LocalDateTime lastVisitDate;
}
