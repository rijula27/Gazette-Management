package spring.aop.gazettemanagementnic.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.service.GCUserService;
import spring.aop.gazettemanagementnic.utils.AesUtil;

@Slf4j
@Component
@RequiredArgsConstructor
public class AesAuthenticationProvider implements AuthenticationProvider {

    private final GCUserService gcUserService;
    private final AesUtil aesUtil;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String encryptedPassword = authentication.getCredentials().toString();

        // Decrypt password received from frontend
        // String rawPassword = aesUtil.decrypt(encryptedPassword);
        log.info("===== AES AUTH PROVIDER =====");
        log.info("Username : {}", username);
        log.info("Encrypted Password : {}", encryptedPassword);

        String rawPassword = aesUtil.decrypt(encryptedPassword);

        log.info("Decrypted Password : {}", rawPassword);

        var user = gcUserService.loadUserByUsername(username);

        if (!gcUserService.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(
                user,
                null,
                user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}