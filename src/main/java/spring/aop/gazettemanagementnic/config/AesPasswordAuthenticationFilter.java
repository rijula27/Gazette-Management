// package spring.aop.gazettemanagementnic.config;

// import java.io.IOException;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import spring.aop.gazettemanagementnic.utils.AesUtil;

// public class AesPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

//     private final AesUtil aesUtil;

//     public AesPasswordAuthenticationFilter(AesUtil aesUtil) {
//         this.aesUtil = aesUtil;
//     }

//     @Override
//     public Authentication attemptAuthentication(
//             HttpServletRequest request,
//             HttpServletResponse response)
//             throws AuthenticationException {

//         String username = obtainUsername(request);
//         String encryptedPassword = obtainPassword(request);

//         String rawPassword;

//         try {
//             rawPassword = aesUtil.decrypt(encryptedPassword);
//         } catch (Exception e) {
//             rawPassword = encryptedPassword;
//         }

//         UsernamePasswordAuthenticationToken authRequest =
//                 UsernamePasswordAuthenticationToken.unauthenticated(
//                         username,
//                         rawPassword);

//         setDetails(request, authRequest);

//         return this.getAuthenticationManager().authenticate(authRequest);
//     }

//     @Override
//     protected void successfulAuthentication(
//             HttpServletRequest request,
//             HttpServletResponse response,
//             FilterChain chain,
//             Authentication authResult)
//             throws IOException, ServletException {

//         super.successfulAuthentication(request, response, chain, authResult);
//     }
// }
