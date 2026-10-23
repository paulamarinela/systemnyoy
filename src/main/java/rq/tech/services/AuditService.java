package rq.tech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rq.tech.models.AuditEntry;
import rq.tech.repositories.AuditLogRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(Long userId, String username, String action, String tableName, Long recordId, String details) {
        AuditEntry entry = AuditEntry.builder()
                .userId(userId)
                .username(username)
                .action(action)
                .tableName(tableName)
                .recordId(recordId)
                .details(sanitize(details))
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(entry);
    }

    public void logRepairAdded(Long userId, String username, Long repairId, String customerName) {
        log(userId, username, "ADD_REPAIR", "repair_jobs", repairId, "Added repair for: " + customerName);
    }

    public void logRepairDeleted(Long userId, String username, Long repairId, String customerName) {
        log(userId, username, "DELETE_REPAIR", "repair_jobs", repairId, "Deleted repair for: " + customerName);
    }

    public void logStatusChange(Long userId, String username, Long repairId, String oldStatus, String newStatus) {
        log(userId, username, "STATUS_CHANGE", "repair_jobs", repairId, "Status: " + oldStatus + " -> " + newStatus);
    }

    public void logPaymentChange(Long userId, String username, Long repairId, String oldStatus, String newStatus, BigDecimal bill) {
        log(userId, username, "PAYMENT_CHANGE", "repair_jobs", repairId, "Payment: " + oldStatus + " -> " + newStatus + " | Bill: " + bill);
    }

    public void logEmailSent(Long userId, String username, Long repairId, String email, String type) {
        log(userId, username, "EMAIL_SENT", "repair_jobs", repairId, "Sent " + type + " to: " + email);
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\p{Cntrl}]", "").trim();
    }
}
