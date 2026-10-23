package rq.tech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rq.tech.models.Repair;
import rq.tech.models.User;
import rq.tech.repositories.RepairRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairService {

    private final RepairRepository repairRepository;
    private final AuditService auditService;
    private final EmailService emailService;
    private final CustomerService customerService;

    @Transactional
    public Repair addRepair(Repair repair, User user) {
        sanitizeRepair(repair);
        Repair saved = repairRepository.save(repair);
        auditService.logRepairAdded(user.getId(), user.getUsername(), saved.getId(), saved.getCustomerName());
        customerService.updateCustomerFromRepair(saved);
        return saved;
    }

    @Transactional
    public boolean updateStatus(Long repairId, String newStatus, User user) {
        Optional<Repair> repairOpt = repairRepository.findById(repairId);
        if (repairOpt.isPresent()) {
            Repair repair = repairOpt.get();
            String oldStatus = repair.getStatus();
            repair.setStatus(newStatus.toUpperCase());
            if ("COMPLETED".equalsIgnoreCase(newStatus)) {
                repair.markAsCompleted();
            }
            repairRepository.save(repair);
            auditService.logStatusChange(user.getId(), user.getUsername(), repairId, oldStatus, newStatus);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updatePaymentStatus(Long repairId, String newStatus, User user) {
        Optional<Repair> repairOpt = repairRepository.findById(repairId);
        if (repairOpt.isPresent()) {
            Repair repair = repairOpt.get();
            String oldStatus = repair.getPaymentStatus();
            repair.setPaymentStatus(newStatus.toUpperCase());
            if ("PAID".equalsIgnoreCase(newStatus)) {
                repair.markAsPaid();
            }
            repairRepository.save(repair);
            auditService.logPaymentChange(user.getId(), user.getUsername(), repairId, oldStatus, newStatus, repair.getBill());
            return true;
        }
        return false;
    }

    @Transactional
    public boolean deleteRepair(Long repairId, User user) {
        Optional<Repair> repairOpt = repairRepository.findById(repairId);
        if (repairOpt.isPresent()) {
            Repair repair = repairOpt.get();
            repairRepository.deleteById(repairId);
            auditService.logRepairDeleted(user.getId(), user.getUsername(), repairId, repair.getCustomerName());
            return true;
        }
        return false;
    }

    public List<Repair> getAllRepairs() {
        return repairRepository.findAll();
    }

    public List<Repair> getPendingRepairs() {
        return repairRepository.findByStatus("PENDING");
    }

    public List<Repair> getOngoingRepairs() {
        return repairRepository.findByStatus("ONGOING");
    }

    public List<Repair> getCompletedRepairs() {
        return repairRepository.findByStatus("COMPLETED");
    }

    public boolean notifyCustomer(Long repairId, User user) {
        Optional<Repair> repairOpt = repairRepository.findById(repairId);
        if (repairOpt.isPresent()) {
            Repair repair = repairOpt.get();
            emailService.sendRepairFinishedEmail(repair);
            auditService.logEmailSent(user.getId(), user.getUsername(), repairId, repair.getCustomerEmail(), "READY_FOR_PICKUP");
            return true;
        }
        return false;
    }

    public long countByStatus(String status) {
        return repairRepository.countByStatus(status.toUpperCase());
    }

    private void sanitizeRepair(Repair repair) {
        repair.setCustomerName(sanitize(repair.getCustomerName()));
        repair.setDevice(sanitize(repair.getDevice()));
        repair.setIssue(sanitize(repair.getIssue()));
        if (repair.getContactNumber() != null) repair.setContactNumber(repair.getContactNumber().trim());
        if (repair.getCustomerEmail() != null) repair.setCustomerEmail(repair.getCustomerEmail().trim().toLowerCase());
        if (repair.getAddress() != null) repair.setAddress(sanitize(repair.getAddress()));
        if (repair.getSerialNumber() != null) repair.setSerialNumber(sanitize(repair.getSerialNumber()));
        if (repair.getImagePath() != null) repair.setImagePath(repair.getImagePath().trim());
    }

    private String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\p{Cntrl}]", "").trim();
    }
}
