package spring.aop.gazettemanagementnic.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import spring.aop.gazettemanagementnic.service.GCUserService;

@Configuration
public class SecurityConfig {

    @Autowired
    private GCUserService gcUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity
                .authenticationProvider(daoAuthenticationProvider())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login","/index","/about","/functions","/gazette/display","/gazette/years/*/months","/gazette/years/*/months/*/dates","/gazette/years/*/months/*/dates/*","/gazette/pdf/**","/tender/display","/tender/years/*/months","/tender/years/*/months/*","/tender/pdf/**","/pdf/**","/tender","/tender/displayArchive","/contactUs","/organizationChart","/policies","/accessibilityStatement","/siteMap","/help","/accessibilityBrowsers","/test","/screenReader","/contact/display","/about/display","/gallery/images/**", "/captcha-image", "/css/**", "/js/**","/images/**").permitAll()
                        .requestMatchers("/gazette/pdf/**","/tender/pdf/**","/creator/tender_delete/**").hasAnyAuthority("CREATOR", "PUBLISHER","ADMIN")
                        .requestMatchers("/creator","/creator_tender","/creator_dashboard","/creator_tender_dashboard","/creator_template","/creator_submission_history","/creator_tender_submission_history","/gazette/upload","/creator/display","/creator/sendPublisher/**","/creator/delete/**","/creator/submission_history","/tender/uploadTender","/creator/tender_display","/creator/sendTenderPublisher/**","/creator/tender_submission_history","/creator/tender_delete/**").hasAuthority("CREATOR")
                        .requestMatchers("/publisher","/publisher_submission_history","/publisher_template","/publisher_tender_submission_history","/publisher_tender","/publisher/publisher_display","/publisher/publisher_delete/**","/publisher/sendBack_Creator/**","/publisher/published/**","/publisher/publisher_submission_history","/publisher/publisher_tender_display","/publisher/sendBack_tender_Creator","/publisher/published_tender/**","/publisher/publisher_tender_submission_history").hasAuthority("PUBLISHER")
                        .requestMatchers("/admin","/admin_tender","/admin_template","/admin_creator_list","/admin/admin_display","/admin/admin_creator_list","/admin/creator_upload","/admin/admin_delete/**","/admin/delete_creator/**","/admin/edit_creator","/admin/admin_tender_display","/upload_pdf","/admin/uploadPdf","/admin_pdf", "/admin/admin_pdf_display","/admin/pdf_delete/**","/admin_contactUs","/contact/save","/contact/edit","/contact/delete/**","/admin_aboutUs","/about/save","/about/aboutDisplay","/admin_gallery","/gallery/upload","/gallery/imageDisplay").hasAuthority("ADMIN")
                        .requestMatchers("/gazette/edit","/tender/edit").hasAnyAuthority("CREATOR", "PUBLISHER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .loginProcessingUrl("/custom_login") // Custom login processing URL
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(gcUserService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
