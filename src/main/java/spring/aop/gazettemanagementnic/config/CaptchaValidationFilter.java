package spring.aop.gazettemanagementnic.config;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CaptchaValidationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Only validate POST /login
        if (request.getServletPath().equals("/login")
                && request.getMethod().equalsIgnoreCase("POST")) {

            String userCaptcha = request.getParameter("captcha");

            String sessionCaptcha =
                    (String) request.getSession().getAttribute("CAPTCHA");

            if (sessionCaptcha == null
                    || userCaptcha == null
                    || !sessionCaptcha.equalsIgnoreCase(userCaptcha)) {

                response.sendRedirect("/login?captchaError=true");
                return;
            }

            // Prevent captcha reuse
            request.getSession().removeAttribute("CAPTCHA");
        }

        filterChain.doFilter(request, response);
    }
}