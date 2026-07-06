package spring.aop.gazettemanagementnic.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.entity.AuditLog;
import spring.aop.gazettemanagementnic.service.AuditService;

@Slf4j
@Controller
@RequestMapping("/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    @GetMapping("/audit-page")
    public String viewAuditLogs(

            @RequestParam(defaultValue = "0", name = "page") int page,

            @RequestParam(required = false) String username,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,

            Model model) {

        Pageable pageable = PageRequest.of(page, 10);

        Page<AuditLog> auditPage = auditService.getAuditLogs(
                username,
                module,
                action,
                status,
                fromDate,
                toDate,
                pageable);

        model.addAttribute("auditPage", auditPage);

        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", auditPage.getTotalPages());

        model.addAttribute("username", username);
        model.addAttribute("module", module);
        model.addAttribute("action", action);
        model.addAttribute("status", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "admin/admin_audit";
    }
}