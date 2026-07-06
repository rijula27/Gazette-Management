package spring.aop.gazettemanagementnic.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import spring.aop.gazettemanagementnic.entity.AuditLog;
import spring.aop.gazettemanagementnic.repository.AuditLogRepository;
import spring.aop.gazettemanagementnic.specification.AuditLogSpecification;

@Service
public class AuditService {

    @Autowired
    private AuditLogRepository repository;

    public void log(String username,
            String module,
            String action,
            String status,
            HttpServletRequest request,
            String description) {

        AuditLog audit = new AuditLog();

        audit.setUsername(username);
        audit.setModule(module);
        audit.setAction(action);
        audit.setStatus(status);
        audit.setDescription(description);
        audit.setIpAddress(request.getRemoteAddr());
        audit.setDateTime(LocalDateTime.now());

        repository.save(audit);
    }

    public Page<AuditLog> getAuditLogs(
            String username,
            String module,
            String action,
            String status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable) {

        return repository.findAll(
                AuditLogSpecification.filter(
                        username,
                        module,
                        action,
                        status,
                        fromDate,
                        toDate),
                pageable);
    }
}
