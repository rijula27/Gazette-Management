// package spring.aop.gazettemanagementnic.config;

// import jakarta.servlet.http.HttpServletRequest;
// import org.springframework.boot.web.servlet.error.ErrorController;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.ResponseBody;

// @Controller
// public class CustomErrorController implements ErrorController {

//     @RequestMapping("${server.error.path:/error}")
//     @ResponseBody
//     public ResponseEntity<String> handleError(HttpServletRequest request) {
//         Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
//         if (statusCode == null) {
//             statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
//         }
//         if (statusCode == null) statusCode = 500;

//         String uri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
//         if (uri == null) uri = request.getRequestURI();

//         // ✅ Convert 500 to 400 for path traversal attempts
//         if (statusCode == 500 && uri != null && (
//                 uri.contains("//") ||
//                 uri.contains("..") ||
//                 uri.contains("/etc/") ||
//                 uri.contains("passwd"))) {
//             return ResponseEntity
//                     .status(HttpStatus.BAD_REQUEST)
//                     .header("X-Frame-Options", "SAMEORIGIN")
//                     .header("Content-Security-Policy", "frame-ancestors 'self'")
//                     .header("X-XSS-Protection", "1; mode=block")
//                     .header("Referrer-Policy", "strict-origin-when-cross-origin")
//                     .body("Bad Request");
//         }

//         return ResponseEntity
//                 .status(statusCode)
//                 .header("X-Frame-Options", "SAMEORIGIN")
//                 .header("Content-Security-Policy", "frame-ancestors 'self'")
//                 .header("X-XSS-Protection", "1; mode=block")
//                 .header("Referrer-Policy", "strict-origin-when-cross-origin")
//                 .body("An error occurred.");
//     }
// }