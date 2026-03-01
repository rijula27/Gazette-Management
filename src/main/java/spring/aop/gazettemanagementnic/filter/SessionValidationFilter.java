package spring.aop.gazettemanagementnic.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/**
 * Session validation filter to enforce session timeout
 * Checks if session is still active and invalidates expired sessions
 */
@WebFilter("/*")
public class SessionValidationFilter implements Filter {

    // Session timeout in milliseconds (30 minutes)
    private static final long SESSION_TIMEOUT_MS = 30 * 60 * 1000;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip filter for public endpoints
        String requestURI = httpRequest.getRequestURI();
        if (isPublicEndpoint(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            // Check if user is logged in
            String loggedInUser = (String) session.getAttribute("loggedInUser");
            
            if (loggedInUser != null) {
                // User is logged in, check if session has timed out
                Long loginTime = (Long) session.getAttribute("loginTime");
                
                if (loginTime != null) {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - loginTime;

                    if (elapsedTime > SESSION_TIMEOUT_MS) {
                        // Session has expired
                        System.out.println("Session timeout detected for user: " + loggedInUser + " (elapsed: " + (elapsedTime / 1000) + " seconds)");
                        
                        // Clear authentication
                        SecurityContextHolder.clearContext();
                        session.removeAttribute("loggedInUser");
                        session.removeAttribute("userRole");
                        session.removeAttribute("loginTime");
                        session.invalidate();
                        
                        // Redirect to login with session expired message
                        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login?session=expired");
                        return;
                    }
                }
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Check if the requested endpoint is public (no authentication required)
     */
    private boolean isPublicEndpoint(String uri) {
        return uri.contains("/login") ||
                uri.contains("/logout") ||
                uri.contains("/index") ||
                uri.contains("/css/") ||
                uri.contains("/js/") ||
                uri.contains("/images/") ||
                uri.contains("/captcha-image") ||
                uri.contains("/static/") ||
                uri.contains("/about") ||
                uri.contains("/functions") ||
                uri.contains("/contactUs") ||
                uri.contains("/gallery/images") ||
                uri.contains("/contact/display") ||
                uri.contains("/about/display") ||
                uri.endsWith("/") ||
                uri.equals("");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
