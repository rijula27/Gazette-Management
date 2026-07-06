package spring.aop.gazettemanagementnic.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.dto.ContactEditDto;
import spring.aop.gazettemanagementnic.dto.ContactUsDto;
import spring.aop.gazettemanagementnic.entity.ContactUs;
import spring.aop.gazettemanagementnic.service.ContactUsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

@Controller
@RequestMapping("/contact")
@Slf4j
public class ContactUsController {

    @Autowired
    private ContactUsService contactUsService;

    // @PostMapping("/save")
    // @ResponseBody
    // public ResponseEntity<String> saveContact(@RequestBody ContactUs contactUs,
    // HttpSession session) {

    // String adminName = (String) session.getAttribute("loggedInUser");

    // try {
    // if (adminName == null || adminName.isEmpty()) {
    // return ResponseEntity.status(401).body("Session expired or not logged in.");
    // }

    // String resultMessage = contactUsService.saveContact(
    // contactUs.getContactTable(),
    // contactUs.getName(),
    // contactUs.getDesignation(),
    // contactUs.getStdCode(),
    // contactUs.getPhno(),
    // contactUs.getMobile(),
    // adminName,
    // LocalDate.now());
    // return ResponseEntity.ok(resultMessage);
    // } catch (Exception e) {
    // log.error("Error occurred while saving contact ", e);
    // return ResponseEntity.status(500).body("Something went wrong. Please try
    // again.");
    // }

    // }

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> saveContact(@Valid @RequestBody ContactUsDto dto,
            Authentication authentication) {

        // log.info("sdklfjslkd ");

        User user = (User) authentication.getPrincipal();
        String adminName = user.getUsername();

        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            String result = contactUsService.saveContact(dto, adminName);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while saving contact", e);
            return ResponseEntity.status(500).body("Something went wrong. Please try again.");
        }
    }

    @GetMapping("/contactDisplay")
    public String contactUs_Display(Model model) {

        List<ContactUs> contact = contactUsService.displayContact();
        model.addAttribute("contacts", contact);
        return "admin/admin_contactUs";

    }

    // @PostMapping("/edit")
    // @ResponseBody
    // public ResponseEntity<String> editContact(@RequestBody ContactUs contactUs,
    // HttpSession session) {

    // String adminName = (String) session.getAttribute("loggedInUser");

    // try {
    // if (adminName == null || adminName.isEmpty()) {
    // return ResponseEntity.status(401).body("Session expired or not logged in.");
    // }

    // String resultMessage = contactUsService.editContact(contactUs.getContactId(),
    // contactUs.getName(),
    // contactUs.getDesignation(), contactUs.getStdCode(), contactUs.getPhno(),
    // contactUs.getMobile(),
    // adminName, LocalDate.now());
    // return ResponseEntity.ok(resultMessage);

    // } catch (Exception e) {
    // log.error("Error occurred while editing contact ", e);
    // return ResponseEntity.status(500).body("Something went wrong. Please try
    // again.");
    // }

    // }

    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> editContact(@Valid @RequestBody ContactEditDto dto,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        String adminName = user.getUsername();

        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            String result = contactUsService.editContact(dto, adminName);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error while editing contact", e);
            return ResponseEntity.status(500).body("Something went wrong. Please try again.");
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteContact(@PathVariable("id") Long id, Model model, Authentication authentication) {
        // String adminName = (String) session.getAttribute("loggedInUser");
        User user = (User) authentication.getPrincipal();
        String adminName = user.getUsername();
        if (adminName != null) {
            contactUsService.deleteContact(id);
            model.addAttribute("successMessage", "User deleted successfully!");
            return "redirect:/contact/contactDisplay";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in

        }

    }

    @GetMapping("/display")
    public String contactUsPage(Model model) {
        List<ContactUs> contact = contactUsService.getAllContact();
        model.addAttribute("contacts", contact);
        return "contactUs";
    }

}
