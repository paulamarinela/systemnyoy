package rq.tech.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rq.tech.dto.RepairReceiptResponse;
import rq.tech.models.Repair;
import rq.tech.repositories.RepairRepository;
import rq.tech.services.AuthService;
import rq.tech.services.BackupService;
import rq.tech.services.ReceiptService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final AuthService authService;
    private final ReceiptService receiptService;
    private final RepairRepository repairRepository;
    private final BackupService backupService;

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (authService.changePassword(userDetails.getUsername(), oldPassword, newPassword)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/receipt/{repairId}")
    public ResponseEntity<RepairReceiptResponse> getReceipt(@PathVariable Long repairId) {
        Optional<Repair> repairOpt = repairRepository.findById(repairId);
        if (repairOpt.isPresent()) {
            String content = receiptService.generateReceiptContent(repairOpt.get());
            return ResponseEntity.ok(new RepairReceiptResponse(content));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/backup")
    public ResponseEntity<String> createBackup() {
        try {
            String backupPath = backupService.createBackup();
            return ResponseEntity.ok("Backup created successfully at: " + backupPath);
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().body("Backup failed: " + e.getMessage());
        }
    }
}
