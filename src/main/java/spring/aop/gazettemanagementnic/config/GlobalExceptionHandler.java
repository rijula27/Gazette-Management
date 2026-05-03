package spring.aop.gazettemanagementnic.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, HttpServletRequest request) {
        logger.error("Exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .header("X-Frame-Options", "SAMEORIGIN")
            .header("Content-Security-Policy", "frame-ancestors 'self'")
            .header("X-XSS-Protection", "1; mode=block")
            // ✅ ADD THIS
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
            // ✅ ADD THIS
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
            // ✅ ADD THIS
            .header("Referrer-Policy", "strict-origin-when-cross-origin")
            .body("Not found.");
    }
}