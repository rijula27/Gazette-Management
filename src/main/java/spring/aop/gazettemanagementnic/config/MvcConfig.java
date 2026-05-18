package spring.aop.gazettemanagementnic.config;

import org.apache.catalina.valves.ErrorReportValve;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

        // ✅ Tomcat error page customizer
        @Bean
        public TomcatServletWebServerFactory tomcatFactory() {
                TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
                factory.addContextCustomizers(context -> {
                        ErrorReportValve errorValve = new ErrorReportValve();
                        errorValve.setShowReport(false);
                        errorValve.setShowServerInfo(false);
                        context.getParent().getPipeline().addValve(errorValve);
                        context.getParent().getPipeline().addValve(new SecurityHeadersValve());
                });
                factory.addConnectorCustomizers(connector -> {
                        connector.setProperty("allowBackslash", "false");
                        connector.setAllowTrace(false);
                });
                return factory;
        }

        // ✅ Path traversal filter — runs before everything
        @Bean
        public FilterRegistrationBean<PathTraversalFilter> pathTraversalFilter() {
                FilterRegistrationBean<PathTraversalFilter> registration = new FilterRegistrationBean<>();
                registration.setFilter(new PathTraversalFilter());
                registration.addUrlPatterns("/*");
                registration.setOrder(1);
                return registration;
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/css/**")
                                .addResourceLocations("classpath:/static/css/");
                registry.addResourceHandler("/js/**")
                                .addResourceLocations("classpath:/static/js/");
                registry.addResourceHandler("/images/**")
                                .addResourceLocations("classpath:/static/images/");
                registry.addResourceHandler("/webfonts/**")
                                .addResourceLocations("classpath:/static/webfonts/");
        }

        public void addViewControllers(ViewControllerRegistry registry) {

                registry.addViewController("/login").setViewName("login");
                registry.addViewController("/index").setViewName("index");
                registry.addViewController("/about").setViewName("about");
                registry.addViewController("/functions").setViewName("functions");
                registry.addViewController("/gazette").setViewName("gazette");
                registry.addViewController("/tender").setViewName("tender");
                registry.addViewController("/tenderArchive").setViewName("tenderArchive");
                registry.addViewController("/contactUs").setViewName("contactUs");
                registry.addViewController("/organizationChart").setViewName("organizationChart");
                registry.addViewController("/policies").setViewName("policies");
                registry.addViewController("/siteMap").setViewName("siteMap");
                registry.addViewController("/policies").setViewName("policies");
                registry.addViewController("/accessibilityStatement").setViewName("accessibilityStatement");
                registry.addViewController("/help").setViewName("help");
                registry.addViewController("/accessibilityBrowsers").setViewName("accessibilityBrowsers");
                registry.addViewController("/screenReader").setViewName("screenReader");
                registry.addViewController("/test").setViewName("test");
                registry.addViewController("/style").setViewName("style");
                registry.addViewController("/logout").setViewName("logout");

                registry.addViewController("/creator").setViewName("creator/creator");
                registry.addViewController("/creator_dashboard").setViewName("creator/creator_dashboard");
                registry.addViewController("/creator_template").setViewName("creator/creator_template");
                registry.addViewController("/creator_submission_history")
                                .setViewName("creator/creator_submission_history");
                registry.addViewController("/creator_tender").setViewName("creator/creator_tender");
                registry.addViewController("/creator_tender_dashboard").setViewName("creator/creator_tender_dashboard");
                registry.addViewController("/creator_tender_submission_history")
                                .setViewName("creator/creator_tender_submission_history");

                registry.addViewController("/publisher").setViewName("publisher/publisher");
                registry.addViewController("/publisher").setViewName("publisher/publisher_template");
                registry.addViewController("/publisher_submission_history")
                                .setViewName("publisher/publisher_submission_history");
                registry.addViewController("/publisher_tender").setViewName("publisher/publisher_tender");
                registry.addViewController("/publisher_tender_submission_history")
                                .setViewName("publisher/publisher_tender_submission_history");

                registry.addViewController("/admin").setViewName("admin/admin");
                registry.addViewController("/admin_template").setViewName("admin/admin_template");
                registry.addViewController("/admin_creator_list").setViewName("admin/admin_creator_list");
                registry.addViewController("/admin_tender").setViewName("admin/admin_tender");
                registry.addViewController("/upload_pdf").setViewName("admin/upload_pdf");
                registry.addViewController("/admin_pdf").setViewName("admin/admin_pdf");
                registry.addViewController("/admin_contactUs").setViewName("admin/admin_contactUs");
                registry.addViewController("/admin_aboutUs").setViewName("admin/admin_aboutUs");
                registry.addViewController("/admin_gallery").setViewName("admin/admin_gallery");
                registry.addViewController("/admin_publisher_list").setViewName("admin/admin_publisher_list");
                registry.addViewController("/test").setViewName("test");
        }
}