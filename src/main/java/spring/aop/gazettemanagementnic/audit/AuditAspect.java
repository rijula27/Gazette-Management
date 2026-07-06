package spring.aop.gazettemanagementnic.audit;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import spring.aop.gazettemanagementnic.service.AuditService;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;
    private final HttpServletRequest request;

    @AfterReturning("@annotation(audit)")
    public void afterSuccess(AuditableAction audit) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getName())) {

            auditService.log(
                    auth.getName(),
                    audit.module(),
                    audit.action(),
                    "SUCCESS",
                    request,
                    audit.description());
        }
    }

    @AfterThrowing(pointcut = "@annotation(audit)", throwing = "ex")
    public void afterFailure(AuditableAction audit, Exception ex) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String username = "UNKNOWN";

        if (auth != null && !"anonymousUser".equals(auth.getName())) {
            username = auth.getName();
        }

        auditService.log(
                username,
                audit.module(),
                audit.action(),
                "FAILED",
                request,
                ex.getMessage());
    }
}