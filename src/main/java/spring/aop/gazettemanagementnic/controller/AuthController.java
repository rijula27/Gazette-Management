package spring.aop.gazettemanagementnic.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    public String showLoginPage(Model model) {
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

        // Validate CAPTCHA: retrieve the generated CAPTCHA from the session
        // String sessionCaptcha = (String) session.getAttribute("captcha");



        // if (sessionCaptcha != null && captchaInput.equals(sessionCaptcha)) {
            if (username != null && username.length() > 9 && username.length() < 15 && username.contains("_")) {
                if (password != null && password.length() == 12) {

                    Optional<GCUser> user = gcUserService.findByUsername(username);


                    // if (user != null && gcUserService.matches(password, user.getPassword())) {
                    if (user.isPresent() && gcUserService.matches(password, user.get().getPassword())) {



                        // Create an authentication token and set it in the security context
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.get().getRole());
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null, Collections.singleton(authority));
                        SecurityContextHolder.getContext().setAuthentication(authToken);


                        request.setAttribute("username", username);
                        request.setAttribute("password", password);
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

        }

    }
