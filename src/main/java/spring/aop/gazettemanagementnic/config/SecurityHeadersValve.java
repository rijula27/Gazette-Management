package spring.aop.gazettemanagementnic.config;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;

import jakarta.servlet.ServletException;
import java.io.IOException;

public class SecurityHeadersValve extends ValveBase {

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        // ✅ Add security headers before processing
        response.addHeader("X-Frame-Options", "SAMEORIGIN");
        response.addHeader("X-XSS-Protection", "1; mode=block");
        response.addHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        // response.addHeader("Content-Security-Policy", "frame-ancestors 'self'");

        if (getNext() != null) {
            getNext().invoke(request, response);
        }
    }
}