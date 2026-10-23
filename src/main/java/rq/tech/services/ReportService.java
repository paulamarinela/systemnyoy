package rq.tech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rq.tech.models.Repair;
import rq.tech.repositories.RepairRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final RepairRepository repairRepository;

    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = repairRepository.calculateTotalRevenue();
        return (revenue != null) ? revenue : BigDecimal.ZERO;
    }

    public List<Repair> getPaidRepairs(LocalDateTime start, LocalDateTime end) {
        return repairRepository.findByDateReceivedBetween(start, end)
                .stream()
                .filter(Repair::isPaid)
                .collect(Collectors.toList());
    }

    public Map<String, Long> getStatusSummary() {
        return Map.of(
                "PENDING", repairRepository.countByStatus("PENDING"),
                "ONGOING", repairRepository.countByStatus("ONGOING"),
                "COMPLETED", repairRepository.countByStatus("COMPLETED")
        );
    }

    public BigDecimal getRevenueBetween(LocalDateTime start, LocalDateTime end) {
        BigDecimal revenue = repairRepository.calculateTotalRevenueBetween(start, end);
        return (revenue != null) ? revenue : BigDecimal.ZERO;
    }
}
