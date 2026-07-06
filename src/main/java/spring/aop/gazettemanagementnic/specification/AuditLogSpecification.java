package spring.aop.gazettemanagementnic.specification;


import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import spring.aop.gazettemanagementnic.entity.AuditLog;

public class AuditLogSpecification {

    public static Specification<AuditLog> filter(
            String username,
            String module,
            String action,
            String status,
            LocalDate fromDate,
            LocalDate toDate) {

        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            if (username != null && !username.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("username")),
                                "%" + username.toLowerCase() + "%"));
            }

            if (module != null && !module.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("module")),
                                "%" + module.toLowerCase() + "%"));
            }

            if (action != null && !action.isBlank()) {
                predicate = cb.and(predicate,
                        cb.like(
                                cb.lower(root.get("action")),
                                "%" + action.toLowerCase() + "%"));
            }

            if (status != null && !status.isBlank()) {
                predicate = cb.and(predicate,
                        cb.equal(root.get("status"), status));
            }

            if (fromDate != null) {
                predicate = cb.and(predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("dateTime"),
                                fromDate.atStartOfDay()));
            }

            if (toDate != null) {
                predicate = cb.and(predicate,
                        cb.lessThanOrEqualTo(
                                root.get("dateTime"),
                                toDate.atTime(23, 59, 59)));
            }

            return predicate;
        };
    }
}
