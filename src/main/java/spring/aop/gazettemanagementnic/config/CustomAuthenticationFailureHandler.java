// package spring.aop.gazettemanagementnic.config;

// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.web.authentication.AuthenticationFailureHandler;
// import org.springframework.stereotype.Component;

// import java.io.IOException;
// import java.net.URLEncoder;

// /**
//  * Custom authentication failure handler to display appropriate error messages
//  * including concurrent session errors
//  */
// @Slf4j
// @Component
// public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

//     @Override
//     public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
//                                        AuthenticationException exception) throws IOException, ServletException {

//     log.error("===== LOGIN FAILED =====", exception);

//                                         log.error("LOGIN FAILED", exception);
//     log.info("Username: {}", request.getParameter("username"));
//     log.info("Password received by Spring Security: {}", request.getParameter("password"));

//         String errorMessage = "Invalid username or password.";

//         // Check for different types of authentication exceptions
//         if (exception instanceof UsernameNotFoundException) {
//             errorMessage = "Invalid username or password.";
//         } else if (exception instanceof BadCredentialsException) {
//             errorMessage = "Invalid username or password.";
//         } else if (exception.getMessage() != null && exception.getMessage().contains("Maximum sessions")) {
//             // Concurrent session error
//             errorMessage = "Your account is already logged in from another device/browser. Please logout from the other session first.";
//         } else if (exception.getMessage() != null) {
//             errorMessage = exception.getMessage();
//         }

//         // Redirect to login page with error message
//         String encodedError = URLEncoder.encode(errorMessage, "UTF-8");
//         response.sendRedirect(request.getContextPath() + "/login?error=" + encodedError);
//     }
// }
