package spring.aop.gazettemanagementnic.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring.aop.gazettemanagementnic.service.AuditService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private AuditService auditService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception)
            throws IOException, ServletException {

        auditService.log(
                request.getParameter("username"),
                "null",
                "LOGIN",
                "FAILED",
                request,
                exception.getMessage());

        if (exception instanceof SessionAuthenticationException) {
            response.sendRedirect("/login?alreadyLoggedIn");
            return;
        }

        response.sendRedirect("/login?error=true");
    }
}
