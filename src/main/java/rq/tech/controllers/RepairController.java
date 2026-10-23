package rq.tech.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import rq.tech.models.Repair;
import rq.tech.models.User;
import rq.tech.services.AuthService;
import rq.tech.services.FileUploadService;
import rq.tech.services.RepairService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final RepairService repairService;
    private final AuthService authService;
    private final FileUploadService fileUploadService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Repair> addRepair(
            @RequestParam("repair") String repairJson,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws IOException {
        User user = authService.findByUsername(userDetails.getUsername()).orElseThrow();
        Repair repair = objectMapper.readValue(repairJson, Repair.class);

        if (photo != null && !photo.isEmpty()) {
            String photoPath = fileUploadService.savePhoto(photo);
            repair.setImagePath(photoPath);
        }

        return ResponseEntity.ok(repairService.addRepair(repair, user));
    }

    @GetMapping
    public ResponseEntity<List<Repair>> getAllRepairs() {
        return ResponseEntity.ok(repairService.getAllRepairs());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRepair(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = authService.findByUsername(userDetails.getUsername()).orElseThrow();
        if (repairService.deleteRepair(id, user)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = authService.findByUsername(userDetails.getUsername()).orElseThrow();
        if (repairService.updateStatus(id, status, user)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<Void> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = authService.findByUsername(userDetails.getUsername()).orElseThrow();
        if (repairService.updatePaymentStatus(id, status, user)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/notify")
    public ResponseEntity<Void> notifyCustomer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = authService.findByUsername(userDetails.getUsername()).orElseThrow();
        if (repairService.notifyCustomer(id, user)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Repair>> getPendingRepairs() {
        return ResponseEntity.ok(repairService.getPendingRepairs());
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<Repair>> getOngoingRepairs() {
        return ResponseEntity.ok(repairService.getOngoingRepairs());
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Repair>> getCompletedRepairs() {
        return ResponseEntity.ok(repairService.getCompletedRepairs());
    }
}
