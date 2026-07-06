// package spring.aop.gazettemanagementnic.config;

// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import lombok.extern.slf4j.Slf4j;

// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.AuthenticationServiceException;
// import org.springframework.security.authentication.BadCredentialsException;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import spring.aop.gazettemanagementnic.utils.AesUtil;

// @Slf4j
// public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

//     private final AuthenticationManager authenticationManager;
//     private final AesUtil aesUtil;

//     public CustomAuthenticationFilter(AuthenticationManager authenticationManager,
//             AesUtil aesUtil) {

//         this.authenticationManager = authenticationManager;
//         this.aesUtil = aesUtil;
//     }

//     @Override
//     public Authentication attemptAuthentication(
//             HttpServletRequest request,
//             HttpServletResponse response)
//             throws AuthenticationException {

//         String username = obtainUsername(request);
//         String encryptedPassword = obtainPassword(request);

//         log.info("======== CustomAuthenticationFilter ========");
//         log.info("Username : {}", username);
//         log.info("Encrypted Password : {}", encryptedPassword);

//         String rawPassword;

//         try {
//             rawPassword = aesUtil.decrypt(encryptedPassword);
//         } catch (Exception ex) {
//             throw new AuthenticationServiceException(
//                     "Unable to decrypt password", ex);
//         }

//         log.info("Raw Password : {}", rawPassword);

//         UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
//                 username,
//                 rawPassword);

//         setDetails(request, authRequest);

//         // Let Spring Security throw BadCredentialsException if needed
//         return authenticationManager.authenticate(authRequest);
//     }
// }