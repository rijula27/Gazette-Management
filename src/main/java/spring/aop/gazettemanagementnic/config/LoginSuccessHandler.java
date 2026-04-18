package spring.aop.gazettemanagementnic.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import spring.aop.gazettemanagementnic.entity.LoginLog;
import spring.aop.gazettemanagementnic.repository.LoginLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private LoginLogRepository loginLogRepository;

    public LoginSuccessHandler(LoginLogRepository loginLogRepository) {
        this.loginLogRepository = loginLogRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        String username = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst().orElse("UNKNOWN");

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        // Save login log
        LoginLog log = new LoginLog();
        log.setUsername(username);
        log.setRole(role);
        log.setLoginTime(LocalDateTime.now());
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);

        loginLogRepository.save(log);

        // Store username and login time in session for timeout validation
        request.getSession().setAttribute("loggedInUser", username);
        request.getSession().setAttribute("userRole", role);
        request.getSession().setAttribute("loginTime", System.currentTimeMillis());

        // Redirect by role
        String targetUrl = switch (role) {
            case "CREATOR" -> "/creator";
            case "PUBLISHER" -> "/publisher/publisher_display";
            case "ADMIN" -> "/admin/admin_display";
            default -> "/index";
        };

        // List<String> allowedUrls = List.of("/creator", "/publisher/publisher_display", "/admin/admin_display",
        //         "/index");

        // if (!allowedUrls.contains(targetUrl)) {
        //     targetUrl = "/index";
        // }

        

        response.sendRedirect(targetUrl);
    }
}
