package spring.aop.gazettemanagementnic.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PathTraversalFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PathTraversalFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();

        if (uri != null && (
                uri.contains("passwd") ||
                uri.contains("/etc/") ||
                uri.contains(".."))) {
            logger.warn("Blocked path traversal: {}", uri);
            res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            res.setHeader("X-Frame-Options", "SAMEORIGIN");
            res.setHeader("Content-Security-Policy", "frame-ancestors 'self'");
            res.setHeader("X-XSS-Protection", "1; mode=block");
            res.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
            res.setContentType("text/plain");
            res.getWriter().write("Bad Request");
            return;
        }

        chain.doFilter(request, response);
    }
}