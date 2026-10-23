package rq.tech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rq.tech.models.Repair;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepairRepository extends JpaRepository<Repair, Long> {
    Optional<Repair> findByUuid(String uuid);
    List<Repair> findByContactNumber(String contactNumber);
    List<Repair> findByCustomerNameContainingIgnoreCase(String name);
    List<Repair> findByStatus(String status);
    List<Repair> findByPaymentStatus(String paymentStatus);

    @Query("SELECT r FROM Repair r WHERE r.dateReceived BETWEEN :startDate AND :endDate")
    List<Repair> findByDateReceivedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(r.bill) FROM Repair r WHERE r.paymentStatus = 'PAID'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT SUM(r.bill) FROM Repair r WHERE r.paymentStatus = 'PAID' AND r.dateReceived BETWEEN :startDate AND :endDate")
    BigDecimal calculateTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    long countByStatus(String status);
}
