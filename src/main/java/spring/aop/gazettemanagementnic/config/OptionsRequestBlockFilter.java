package spring.aop.gazettemanagementnic.config;

import java.io.IOException;
import java.util.Set;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OptionsRequestBlockFilter extends OncePerRequestFilter {

    private static final Set<String> BLOCKED_METHODS = Set.of(
            "OPTIONS",
            "PUT",
            "DELETE",
            "PATCH",
            "CONNECT");

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("Method = '" + request.getMethod() + "'");

        if (BLOCKED_METHODS.contains(request.getMethod())) {

            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.setContentType("text/plain");
            response.getWriter().write("Method Not Allowed");
            response.flushBuffer();

            return;
        }

        filterChain.doFilter(request, response);
    }
}