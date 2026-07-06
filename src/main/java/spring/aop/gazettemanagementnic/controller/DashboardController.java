package spring.aop.gazettemanagementnic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        return switch (role) {
            case "ADMIN" -> "redirect:/admin/admin_display";
            case "CREATOR" -> "redirect:/creator";
            case "PUBLISHER" -> "redirect:/publisher/publisher_display";
            default -> "redirect:/login";
        };
    }
}