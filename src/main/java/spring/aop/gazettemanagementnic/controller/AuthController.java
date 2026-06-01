package spring.aop.gazettemanagementnic.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// import com.google.common.base.Optional;

import java.util.Optional;

import javax.imageio.ImageIO;

import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.service.AuthService;
import spring.aop.gazettemanagementnic.service.GCUserService;

import java.io.IOException;
import java.util.Collections;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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

        // Create CAPTCHA only if not already present
        String captcha = (String) httpSession.getAttribute("CAPTCHA");

        if (captcha == null) {
            captcha = authService.generateCaptcha();
            httpSession.setAttribute("CAPTCHA", captcha);
        }

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
                    // session.removeAttribute("CAPTCHA");
                    session.removeAttribute("CAPTCHA");
                    request.changeSessionId();
                    return "forward:/custom_login";

                } else {
                    refreshCaptcha(session);
                    model.addAttribute("error", "Invalid username or password.");
                    return "login";
                }
            } else {
                refreshCaptcha(session);
                // model.addAttribute("error", "Password must be exactly 12 characters long.");
                model.addAttribute("error", "Invalid username or password.");
                return "login";
            }

        } else {
            // model.addAttribute("error", "Username must be 10 to 15 characters long and
            // contain an underscore.");
            refreshCaptcha(session);
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

    private void refreshCaptcha(HttpSession session) {

        String captcha = authService.generateCaptcha();

        session.setAttribute("CAPTCHA", captcha);
    }

    @GetMapping("/captcha-image")
    @ResponseBody
    public ResponseEntity<byte[]> captchaImage(HttpSession session)
            throws IOException {

        String captcha = (String) session.getAttribute("CAPTCHA");

        if (captcha == null) {
            captcha = authService.generateCaptcha();
            session.setAttribute("CAPTCHA", captcha);
        }

        BufferedImage image = new BufferedImage(
                160,
                50,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D g = image.createGraphics();

        // Background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 160, 50);

        // Noise lines
        for (int i = 0; i < 6; i++) {

            g.setColor(new Color(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)));

            g.drawLine(
                    (int) (Math.random() * 160),
                    (int) (Math.random() * 50),
                    (int) (Math.random() * 160),
                    (int) (Math.random() * 50));
        }

        // Random dots
        for (int i = 0; i < 60; i++) {

            g.setColor(new Color(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)));

            g.fillOval(
                    (int) (Math.random() * 160),
                    (int) (Math.random() * 50),
                    2,
                    2);
        }

        // Slight rotation
        g.rotate(
                Math.toRadians(-5 + (Math.random() * 10)),
                80,
                25);

        // CAPTCHA text
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(Color.BLACK);
        g.drawString(captcha, 20, 35);

        g.dispose();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();

        ImageIO.write(image, "png", baos);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.IMAGE_PNG);

        headers.setCacheControl(
                "no-cache, no-store, must-revalidate");

        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(baos.toByteArray());
    }

    @GetMapping("/refresh-captcha")
    @ResponseBody
    public ResponseEntity<Void> refreshCaptchaEndpoint(
            HttpSession session) {


        refreshCaptcha(session);

        return ResponseEntity.ok().build();
    }
}
