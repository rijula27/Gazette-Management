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
import spring.aop.gazettemanagementnic.service.GCUserService;

import java.util.Collections;

@Controller
public class AuthController {

    @Autowired
    private GCUserService gcUserService;

    // Display the login page (the CAPTCHA image is served separately)
    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "session", required = false) String session) {
        
        // Handle different redirect scenarios
        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }
        
        if ("expired".equals(session)) {
            model.addAttribute("sessionExpired", "Your session has timed out. Please login again.");
        }
        
        if ("true".equals(logout)) {
            model.addAttribute("logoutSuccess", "You have been successfully logged out.");
        }
        
        return "login";
    }

    // Process login submission
//    @PostMapping("/perform_login")
    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model,
                               HttpServletRequest request) {

        // if (sessionCaptcha != null && captchaInput.equals(sessionCaptcha)) {
            if (username != null && username.length() > 9 && username.length() < 21 && username.contains("_")) {
                if (password != null && password.length() == 12) {

                    Optional<GCUser> user = gcUserService.findByUsername(username);


                    // if (user != null && gcUserService.matches(password, user.getPassword())) {
                    if (user.isPresent() && gcUserService.matches(password, user.get().getPassword())) {



                        // Create an authentication token and set it in the security context
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.get().getRole());
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));
                        SecurityContextHolder.getContext().setAuthentication(authToken);


                        // request.setAttribute("username", username);
                        // request.setAttribute("password", password);
                        return "forward:/custom_login";

                    } else {
                        model.addAttribute("error", "Invalid username or password.");
                        return "login";
                    }
                } else {
//                    model.addAttribute("error", "Password must be exactly 12 characters long.");
                    model.addAttribute("error", "Invalid username or password.");
                    return "login";
                }

            } else {
//                model.addAttribute("error", "Username must be 10 to 15 characters long and contain an underscore.");
                model.addAttribute("error", "Invalid username or password.");
                return "login";
            }

        // } else {
        //     model.addAttribute("error", "Invalid CAPTCHA. Please try again.");
        //     return "login";
        // }

        // }

    }

    /**
     * Logout endpoint - properly invalidates session and clears authentication
     */
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
}
