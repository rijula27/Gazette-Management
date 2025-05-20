//package spring.aop.gazettemanagementnic.controller;
//
//
//import org.springframework.ui.Model;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.boot.web.servlet.error.ErrorController;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@Controller
//    public class CustomErrorController implements ErrorController {
//
//        @RequestMapping("/error")
//        public String handleError(HttpServletRequest request, Model model) {
//            // You can get error attributes from the request if needed
//            Object status = request.getAttribute("javax.servlet.error.status_code");
//            Object error = request.getAttribute("javax.servlet.error.error");
//            Object message = request.getAttribute("javax.servlet.error.message");
//
//
//            model.addAttribute("error",error);
//            model.addAttribute("error", error);
//            model.addAttribute("message", message);
//
//            // Return error.html which will be rendered by Thymeleaf
//            return "error";
//        }
//
//        // For Spring Boot 2.3 and below, you might need to override getErrorPath()
//        // However, in Spring Boot 2.4 and later, this method is deprecated and not necessary.
//    }
//
//
