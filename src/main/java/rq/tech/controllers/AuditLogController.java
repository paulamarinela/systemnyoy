package rq.tech.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rq.tech.models.AuditEntry;
import rq.tech.repositories.AuditLogRepository;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ResponseEntity<List<AuditEntry>> getAllLogs() {
        return ResponseEntity.ok(auditLogRepository.findAll());
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditEntry>> getLogsByUser(@PathVariable String username) {
        return ResponseEntity.ok(auditLogRepository.findByUsername(username));
    }
}
