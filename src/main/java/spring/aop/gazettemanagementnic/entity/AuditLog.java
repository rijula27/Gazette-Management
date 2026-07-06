package spring.aop.gazettemanagementnic.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "module", length = 30)
    private String module;

    // getters & setters
}