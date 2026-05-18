package spring.aop.gazettemanagementnic.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, HttpServletRequest request) {

        logger.error("Exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        String uri = request.getRequestURI();
        String contentType = request.getContentType();

        // ✅ Method not allowed (POST to GET-only endpoints)
        if (ex.getClass().getSimpleName().equals("HttpRequestMethodNotAllowedException")) {
            return ResponseEntity
                    .status(HttpStatus.METHOD_NOT_ALLOWED)
                    .header("X-Frame-Options", "SAMEORIGIN")
                    .header("Content-Security-Policy", "frame-ancestors 'self'")
                    .header("X-XSS-Protection", "1; mode=block")
                    .header("Referrer-Policy", "strict-origin-when-cross-origin")
                    .body("Method Not Allowed");
        }

        // ✅ Path traversal
        // ✅ Path traversal
        if (uri != null && (uri.contains("//") ||
                uri.contains("..") ||
                uri.contains("/etc") || // ← remove trailing slash
                uri.contains("passwd") || // ← add this
                uri.contains("\\"))) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("X-Frame-Options", "SAMEORIGIN")
                    .header("Content-Security-Policy", "frame-ancestors 'self'")
                    .header("X-XSS-Protection", "1; mode=block")
                    .header("Referrer-Policy", "strict-origin-when-cross-origin")
                    .body("Bad Request");
        }

        // ✅ XXE / malicious XML
        if (contentType != null && contentType.contains("xml")) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header("X-Frame-Options", "SAMEORIGIN")
                    .header("Content-Security-Policy", "frame-ancestors 'self'")
                    .header("X-XSS-Protection", "1; mode=block")
                    .header("Referrer-Policy", "strict-origin-when-cross-origin")
                    .body("Bad Request");
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Frame-Options", "SAMEORIGIN")
                .header("Content-Security-Policy", "frame-ancestors 'self'")
                .header("X-XSS-Protection", "1; mode=block")
                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                .body("An error occurred.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        logger.error("IllegalArgument at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("X-Frame-Options", "SAMEORIGIN")
                .header("Content-Security-Policy", "frame-ancestors 'self'")
                .header("X-XSS-Protection", "1; mode=block")
                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                .body("Bad Request");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFound(NoResourceFoundException ex) {
        logger.debug("Static resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("X-Frame-Options", "SAMEORIGIN")
                .header("Content-Security-Policy", "frame-ancestors 'self'")
                .header("X-XSS-Protection", "1; mode=block")
                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                .body("Not found.");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        logger.debug("No handler found for: {}", request.getRequestURI());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("X-Frame-Options", "SAMEORIGIN")
                .header("Content-Security-Policy", "frame-ancestors 'self'")
                .header("X-XSS-Protection", "1; mode=block")
                .header("Referrer-Policy", "strict-origin-when-cross-origin")
                .body("Not found.");
    }
}