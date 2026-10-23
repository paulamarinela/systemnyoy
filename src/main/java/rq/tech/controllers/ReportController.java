package rq.tech.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rq.tech.models.Repair;
import rq.tech.services.ReportService;
import rq.tech.services.StorageService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final StorageService storageService;

    @GetMapping("/revenue")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        return ResponseEntity.ok(reportService.getTotalRevenue());
    }

    @GetMapping("/revenue/period")
    public ResponseEntity<BigDecimal> getRevenueBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(reportService.getRevenueBetween(start, end));
    }

    @GetMapping("/paid-repairs")
    public ResponseEntity<List<Repair>> getPaidRepairs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(reportService.getPaidRepairs(start, end));
    }

    @GetMapping("/status-summary")
    public ResponseEntity<Map<String, Long>> getStatusSummary() {
        return ResponseEntity.ok(reportService.getStatusSummary());
    }

    @GetMapping("/storage-status")
    public ResponseEntity<String> getStorageStatus() {
        return ResponseEntity.ok(storageService.getStorageStatus());
    }

    @GetMapping("/free-space")
    public ResponseEntity<Long> getFreeSpace() {
        return ResponseEntity.ok(storageService.getFreeSpaceInMB());
    }
}
