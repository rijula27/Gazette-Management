package spring.aop.gazettemanagementnic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import spring.aop.gazettemanagementnic.entity.AuditLog;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long>,
                JpaSpecificationExecutor<AuditLog> {

}
