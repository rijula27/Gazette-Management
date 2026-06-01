package spring.aop.gazettemanagementnic.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// import com.google.common.base.Optional;

import java.util.Optional;

import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.service.AuthService;
import spring.aop.gazettemanagementnic.service.GCUserService;

import java.util.Collections;

@Controller
public class AuthController {

    @Autowired
    private GCUserService gcUserService;

    @Autowired
    private AuthService authService;


    @GetMapping("/login")
    public String showLoginPage(
            Model model,
            HttpSession httpSession,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "session", required = false) String sessionParam) {

        // Handle login error
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }

        // Handle session timeout
        if ("expired".equals(sessionParam)) {
            model.addAttribute(
                    "sessionExpired",
                    "Your session has timed out. Please login again.");
        }

        // Handle logout success
        if ("true".equals(logout)) {
            model.addAttribute(
                    "logoutSuccess",
                    "You have been successfully logged out.");
        }

        // Generate new CAPTCHA
        String captcha = authService.generateCaptcha();

        // Store CAPTCHA in session
        httpSession.setAttribute("CAPTCHA", captcha);

        // Send CAPTCHA to view
        model.addAttribute("captcha", captcha);

        return "login";
    }

    // Process login submission
    // @PostMapping("/perform_login")
    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("captcha") String captchaInput,
            HttpSession session,
            Model model,
            HttpServletRequest request) {

        String sessionCaptcha = (String) session.getAttribute("CAPTCHA");

        if (sessionCaptcha == null ||
                !sessionCaptcha.equals(captchaInput)) {

            model.addAttribute("error", "Invalid CAPTCHA");

            String newCaptcha = authService.generateCaptcha();

            session.setAttribute("CAPTCHA", newCaptcha);
            model.addAttribute("captcha", newCaptcha);

            return "login";
        }

        // if (sessionCaptcha != null && captchaInput.equals(sessionCaptcha)) {
        if (username != null && username.length() > 9 && username.length() < 21 && username.contains("_")) {
            if (password != null && password.length() == 12) {

                Optional<GCUser> user = gcUserService.findByUsername(username);

                // if (user != null && gcUserService.matches(password, user.getPassword())) {
                if (user.isPresent() && gcUserService.matches(password, user.get().getPassword())) {

                    // Create an authentication token and set it in the security context
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.get().getRole());
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username,
                            null, Collections.singleton(authority));
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // request.setAttribute("username", username);
                    // request.setAttribute("password", password);
                    session.removeAttribute("CAPTCHA");
                    return "forward:/custom_login";

                } else {
                    refreshCaptcha(session, model);
                    model.addAttribute("error", "Invalid username or password.");
                    return "login";
                }
            } else {
                refreshCaptcha(session, model);
                // model.addAttribute("error", "Password must be exactly 12 characters long.");
                model.addAttribute("error", "Invalid username or password.");
                return "login";
            }

        } else {
            // model.addAttribute("error", "Username must be 10 to 15 characters long and
            // contain an underscore.");
            refreshCaptcha(session, model);
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        // Invalidate the current session
        if (session != null) {
            // Remove user attributes
            session.removeAttribute("loggedInUser");
            session.removeAttribute("userRole");
            session.removeAttribute("loginTime");
            // Invalidate the entire session
            session.invalidate();
        }

        // Clear the security context
        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext().setAuthentication(null);

        // Redirect to login page with logout message
        return "redirect:/login?logout=true";
    }

    private void refreshCaptcha(HttpSession session, Model model) {
        String captcha = authService.generateCaptcha();
        session.setAttribute("CAPTCHA", captcha);
        model.addAttribute("captcha", captcha);
    }
}
