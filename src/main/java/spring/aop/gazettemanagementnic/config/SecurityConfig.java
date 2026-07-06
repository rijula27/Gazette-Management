package spring.aop.gazettemanagementnic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.service.GCUserService;
import spring.aop.gazettemanagementnic.utils.AesUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Slf4j
@Configuration
public class SecurityConfig {

        @Autowired
        private GCUserService gcUserService;

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private AesAuthenticationProvider aesAuthenticationProvider;

        @Autowired
        private CaptchaValidationFilter captchaValidationFilter;

        @Autowired
        private LoginSuccessHandler loginSuccessHandler;

        @Autowired
        private LoginFailureHandler loginFailureHandler;

        @Autowired
        private OptionsRequestBlockFilter optionsRequestBlockFilter;

        // @Autowired
        // private LoginSuccessHandler loginSuccessHandler;

        // @Autowired
        // private CustomAuthenticationFailureHandler authenticationFailureHandler;

        // @Autowired
        // private AesUtil aesUtil;

        // @Bean
        // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws
        // Exception {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/captcha-image")) // Enable CSRF for all
                                                                                              // endpoints except
                                                                                              // captcha-image
                                // .authenticationProvider(daoAuthenticationProvider())
                                .authenticationProvider(aesAuthenticationProvider)

                                // Block OPTIONS requests FIRST
                                .addFilterBefore(
                                                optionsRequestBlockFilter,
                                                UsernamePasswordAuthenticationFilter.class)

                                .addFilterBefore(
                                                captchaValidationFilter,
                                                UsernamePasswordAuthenticationFilter.class)
                                // .headers(headers -> headers
                                // .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Prevent
                                // // clickjacking
                                // // .xssProtection(Customizer.withDefaults()) // Enable XSS protection

                                // .xssProtection(xss -> xss.headerValue(
                                // XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                                // .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                                // ✅ CORRECT
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                                                .xssProtection(xss -> xss.headerValue(
                                                                XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                                                .contentSecurityPolicy(csp -> csp
                                                                .policyDirectives(
                                                                                "default-src 'self'; " +
                                                                                                "script-src 'self'; " +
                                                                                                "style-src 'self'; " +
                                                                                                "img-src 'self' data:; "
                                                                                                +
                                                                                                "font-src 'self' data:; "
                                                                                                +
                                                                                                "connect-src 'self'; " +
                                                                                                "object-src 'none'; " +
                                                                                                "base-uri 'self'; " +
                                                                                                "frame-ancestors 'self'; "
                                                                                                +
                                                                                                "form-action 'self';"))

                                                .referrerPolicy(referrer -> referrer
                                                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)))

                                .authorizeHttpRequests(authorize -> authorize
                                                // .requestMatchers(HttpMethod.OPTIONS, "/**").authenticated()

                                                .requestMatchers("/login", "/index", "/", "/home", "/about",
                                                                "/functions", "/gazette/display",
                                                                "/gazette/years/*/months",
                                                                "/gazette/years/*/months/*/dates",
                                                                "/gazette/years/*/months/*/dates/*", "/tender/display",
                                                                "/tender/years/*/months", "/tender/years/*/months/*",
                                                                "/tender", "/tender/displayArchive", "/contactUs",
                                                                "/organizationChart", "/policies",
                                                                "/accessibilityStatement", "/siteMap", "/help",
                                                                "/accessibilityBrowsers",
                                                                "/test", "/screenReader",
                                                                "/contact/display", "/about/display",
                                                                "/gallery/images/**", "/captcha-image",
                                                                "/refresh-captcha", "/css/**",
                                                                "/webfonts/**",
                                                                "/js/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers("/gazette/pdf/**", "/tender/pdf/**", "/pdf/**")
                                                .permitAll() // Require authentication for PDF viewing
                                                .requestMatchers("/creator", "/creator_tender", "/creator_dashboard",
                                                                "/creator_tender_dashboard", "/creator_template",
                                                                "/creator_submission_history",
                                                                "/creator_tender_submission_history", "/gazette/upload",
                                                                "/creator/display", "/creator/sendPublisher/**",
                                                                "/creator/delete/**", "/creator/submission_history",
                                                                "/tender/uploadTender", "/creator/tender_display",
                                                                "/creator/sendTenderPublisher/**",
                                                                "/creator/tender_submission_history",
                                                                "/creator/tender_delete/**")
                                                .hasAuthority("CREATOR")
                                                .requestMatchers("/publisher", "/publisher_submission_history",
                                                                "/publisher_template",
                                                                "/publisher_tender_submission_history",
                                                                "/publisher_tender", "/publisher/publisher_display",
                                                                "/publisher/publisher_delete/**",
                                                                "/publisher/tender_delete/**",
                                                                "/publisher/sendBack_Creator/**",
                                                                "/publisher/published/**",
                                                                "/publisher/publisher_submission_history",
                                                                "/publisher/publisher_tender_display",
                                                                "/publisher/sendBack_tender_Creator",
                                                                "/publisher/published_tender/**",
                                                                "/publisher/publisher_tender_submission_history")
                                                .hasAuthority("PUBLISHER")
                                                .requestMatchers("/admin", "/admin_tender", "/admin_template",
                                                                "/admin_creator_list", "/admin/admin_display",
                                                                "/admin/admin_creator_list",
                                                                "/admin/admin_publisher_list", "/admin/creator_upload",
                                                                "/admin/admin_delete/**", "/admin/delete_creator/**",
                                                                "/admin/edit_creator", "/admin/admin_tender_display",
                                                                "/upload_pdf", "/admin/uploadPdf", "/admin_pdf",
                                                                "/admin/admin_pdf_display", "/admin/pdf_delete/**",
                                                                "/admin_contactUs", "/contact/save", "/contact/edit",
                                                                "/contact/delete/**", "/admin_aboutUs", "/about/save",
                                                                "/about/aboutDisplay", "/admin_gallery",
                                                                "/gallery/upload",
                                                                "/gallery/imageDisplay",
                                                                "/audit/audit-page")
                                                .hasAuthority("ADMIN")
                                                .requestMatchers("/gazette/edit", "/tender/edit")
                                                .hasAnyAuthority("CREATOR", "PUBLISHER")
                                                .anyRequest().authenticated())
                                // .formLogin(form -> form
                                // .loginPage("/login")
                                // .loginProcessingUrl("/login")
                                // .defaultSuccessUrl("/dashboard", true)
                                // .failureUrl("/login?error=true")
                                // .permitAll())

                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .successHandler(loginSuccessHandler)
                                                .failureHandler(loginFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true")
                                                .invalidateHttpSession(true) // Invalidate session
                                                .clearAuthentication(true) // Clear authentication in SecurityContext
                                                .deleteCookies("JSESSIONID") // Delete session cookie
                                                .permitAll());
                // .sessionManagement(session -> session
                // // Generate new session ID after login (prevents session fixation
                // // attacks)
                // .invalidSessionUrl("/login?session=expired")
                // .sessionConcurrency(concurrency -> concurrency
                // .sessionRegistry(sessionRegistry()) // Use
                // // SessionRegistry
                // // to track sessions
                // .maximumSessions(1) // Only 1 active session per user
                // .maxSessionsPreventsLogin(true) // Prevent new login if
                // // already logged in
                // // elsewhere
                // .expiredUrl("/login?session=expired") // Redirect when
                // // forced logout
                // ));

                // CustomAuthenticationFilter customAuthenticationFilter = new
                // CustomAuthenticationFilter(
                // authenticationManager, aesUtil);

                // customAuthenticationFilter.setFilterProcessesUrl("/custom_login");

                // customAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);

                // customAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

                // http.addFilterAt(
                // customAuthenticationFilter,
                // UsernamePasswordAuthenticationFilter.class);

                http
                                // your existing configuration
                                .sessionManagement(session -> session
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(true) // expire old session and allow new
                                                                                 // login
                                );

                return http.build();
        }

        // @Bean
        // public DaoAuthenticationProvider daoAuthenticationProvider() {
        // DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // provider.setUserDetailsService(gcUserService);
        // provider.setPasswordEncoder(passwordEncoder);
        // return provider;
        // }

        @Bean
        public AuthenticationManager authenticationManager(
                        AuthenticationConfiguration configuration)
                        throws Exception {

                return configuration.getAuthenticationManager();
        }

        @Bean
        public SessionRegistry sessionRegistry() {
                return new SessionRegistryImpl();
        }

        @Bean
        public HttpSessionEventPublisher httpSessionEventPublisher() {
                // Required for concurrent session handling to work properly
                return new HttpSessionEventPublisher();
        }
}
