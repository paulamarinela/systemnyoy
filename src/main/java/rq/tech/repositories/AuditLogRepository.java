package rq.tech.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rq.tech.models.AuditEntry;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditEntry, Long> {
    List<AuditEntry> findByUsername(String username);
    List<AuditEntry> findByTableName(String tableName);
    List<AuditEntry> findByRecordId(Long recordId);
}
