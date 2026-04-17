package spring.aop.gazettemanagementnic.controller;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.dto.CreatorRequestDTO;
import spring.aop.gazettemanagementnic.dto.EditCreatorRequestDTO;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.Pdf;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.service.GCUserService;
import spring.aop.gazettemanagementnic.service.GazetteService;
import spring.aop.gazettemanagementnic.service.PdfService;
import spring.aop.gazettemanagementnic.service.TenderService;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private GCUserService gcUserService;

    @Autowired
    private GazetteService gazetteService;

    @Autowired
    private TenderService tenderService;

    // @Autowired
    // private GCUserRepository gcUserRepository;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/admin_display")
    public String displayGazette_Admin(Model model, HttpSession session) {

        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<Gazette> gazettes = gazetteService.displayGazette_Admin();
            model.addAttribute("gazettes", gazettes);
            return "admin/admin";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in
        }

    }

    @GetMapping("/admin_creator_list")
    public String displayCreator_List(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<GCUser> gcUsers = gcUserService.displayUser_List();
            model.addAttribute("gcUsers", gcUsers);
            return "admin/admin_creator_list";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in
        }
    }

    @PostMapping("/creator_upload")
    @ResponseBody
    public ResponseEntity<String> creatorUpload(@RequestBody CreatorRequestDTO requestDTO,
            HttpSession session) {
        String adminName = (String) session.getAttribute("loggedInUser");

        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            // Optional<GCUser> optionalAdmin = gcUserRepository.findByUsername(adminName);
            Optional<GCUser> optionalAdmin = gcUserService.findByUsername(adminName);
            if (!optionalAdmin.isPresent()) {
                return ResponseEntity.status(404).body("Admin user not found.");
            }

            String existingAdminPassword = optionalAdmin.get().getPassword();
            String rawAdminPassword = requestDTO.getAdminPassword();

            if (!gcUserService.matches(rawAdminPassword, existingAdminPassword)) {
                return ResponseEntity.badRequest().body("Wrong Admin password");
            }

            if (!requestDTO.getUserPassword().equals(requestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            if (!requestDTO.getUserPassword().equals(requestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            String resultMessage = gcUserService.saveUser(
                    requestDTO.getUserName(),
                    requestDTO.getUserPassword(),
                    adminName,
                    LocalDate.now());

            return ResponseEntity.ok(resultMessage);

        } catch (FileAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("A user with this username already exists.");
        } catch (Exception e) {
            log.error("Error occurred while creating creator", e);
            // return ResponseEntity.status(500).body("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }

    }

    @GetMapping("/admin_delete/{id}")
    public String deleteAdminGazette(@PathVariable("id") Long id, Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            gazetteService.deleteGazette(id);
            model.addAttribute("successMessage", "Gazette deleted successfully!");
            return "redirect:/admin/admin_display";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in

        }
    }

    @GetMapping("/delete_creator/{id}")
    public String deleteCreator(@PathVariable("id") Long id, Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            gcUserService.deleteUser(id);
            model.addAttribute("successMessage", "User deleted successfully!");
            return "redirect:/admin/admin_creator_list";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in

        }

    }

    @PostMapping("/edit_creator")
    @ResponseBody
    public ResponseEntity<String> edit_creator(@RequestBody EditCreatorRequestDTO editRequestDTO,
            HttpSession session) {
        String adminName = (String) session.getAttribute("loggedInUser");
        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            // Optional<GCUser> optionalAdmin = gcUserRepository.findByUsername(adminName);
            Optional<GCUser> optionalAdmin = gcUserService.findByUsername(adminName);
            if (!optionalAdmin.isPresent()) {
                return ResponseEntity.status(404).body("Admin user not found.");
            }

            String existingAdminPassword = optionalAdmin.get().getPassword();
            String rawAdminPassword = editRequestDTO.getAdminPassword();

            if (!gcUserService.matches(rawAdminPassword, existingAdminPassword)) {
                return ResponseEntity.badRequest().body("Wrong Admin password");
            }

            if (!editRequestDTO.getNewUserPassword().equals(editRequestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            if (editRequestDTO.getNewUserPassword().equals(editRequestDTO.getUserConfirmPassword())) {

                ResponseEntity<String> message = gcUserService.editCreator(
                        editRequestDTO.getUserName(),
                        editRequestDTO.getNewUserName(),
                        editRequestDTO.getExistingUserPassword(),
                        editRequestDTO.getNewUserPassword(),
                        editRequestDTO.getUserConfirmPassword(),
                        LocalDate.now());

                return (message);
            } else {

                return ResponseEntity.badRequest().body(" Password and Confirm Password don't match");
            }

        } catch (Exception e) {
            log.error("Error occurred while editing creator", e);
            // return ResponseEntity.status(500).body("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }
    }

    @GetMapping("/admin_publisher_list")
    public String displayPublisher_List(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<GCUser> gcUsers = gcUserService.displayPublisher_List();
            model.addAttribute("gcUsers", gcUsers);
            return "admin/admin_publisher_list";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in
        }
    }

    @PostMapping("/publisher_upload")
    @ResponseBody
    public ResponseEntity<String> publisherUpload(@RequestBody CreatorRequestDTO requestDTO,
            HttpSession session) {
        String adminName = (String) session.getAttribute("loggedInUser");

        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            // Optional<GCUser> optionalAdmin = gcUserRepository.findByUsername(adminName);
            Optional<GCUser> optionalAdmin = gcUserService.findByUsername(adminName);
            if (!optionalAdmin.isPresent()) {
                return ResponseEntity.status(404).body("Admin user not found.");
            }

            String existingAdminPassword = optionalAdmin.get().getPassword();
            String rawAdminPassword = requestDTO.getAdminPassword();

            if (!gcUserService.matches(rawAdminPassword, existingAdminPassword)) {
                return ResponseEntity.badRequest().body("Wrong Admin password");
            }

            if (!requestDTO.getUserPassword().equals(requestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            if (!requestDTO.getUserPassword().equals(requestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            String resultMessage = gcUserService.savePublisher(
                    requestDTO.getUserName(),
                    requestDTO.getUserPassword(),
                    adminName,
                    LocalDate.now());

            return ResponseEntity.ok(resultMessage);

        } catch (FileAlreadyExistsException e) {
            return ResponseEntity.badRequest().body("A user with this username already exists.");
        } catch (Exception e) {
            log.error("Error occurred while creating publisher user ", e);
            return ResponseEntity.status(500).body("Something went wrong. Please try again.");
        }

    }

    @GetMapping("/delete_publisher/{id}")
    public String deletePublisher(@PathVariable("id") Long id, Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            gcUserService.deleteUser(id);
            model.addAttribute("successMessage", "User deleted successfully!");
            return "redirect:/admin/admin_publisher_list";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in

        }

    }

    @PostMapping("/edit_publisher")
    @ResponseBody
    public ResponseEntity<String> edit_publisher(@RequestBody EditCreatorRequestDTO editRequestDTO,
            HttpSession session) {
        String adminName = (String) session.getAttribute("loggedInUser");
        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            // Optional<GCUser> optionalAdmin = gcUserRepository.findByUsername(adminName);
            Optional<GCUser> optionalAdmin = gcUserService.findByUsername(adminName);
            if (!optionalAdmin.isPresent()) {
                return ResponseEntity.status(404).body("Admin user not found.");
            }

            String existingAdminPassword = optionalAdmin.get().getPassword();
            String rawAdminPassword = editRequestDTO.getAdminPassword();

            if (!gcUserService.matches(rawAdminPassword, existingAdminPassword)) {
                return ResponseEntity.badRequest().body("Wrong Admin password");
            }

            if (!editRequestDTO.getNewUserPassword().equals(editRequestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            if (editRequestDTO.getNewUserPassword().equals(editRequestDTO.getUserConfirmPassword())) {

                ResponseEntity<String> message = gcUserService.editCreator(
                        editRequestDTO.getUserName(),
                        editRequestDTO.getNewUserName(),
                        editRequestDTO.getExistingUserPassword(),
                        editRequestDTO.getNewUserPassword(),
                        editRequestDTO.getUserConfirmPassword(),
                        LocalDate.now());

                return (message);
            } else {

                return ResponseEntity.badRequest().body(" Password and Confirm Password don't match");
            }

        } catch (Exception e) {
            log.error("Error occurred while editing Publisher ", e);
            // return ResponseEntity.status(500).body("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }
    }

    @GetMapping("/admin_tender_display")
    public String display_tender_Admin(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<Tender> tenders = tenderService.display_Tender_Admin();
            model.addAttribute("tenders", tenders);
            return "admin/admin_tender";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in
        }
    }

    @PostMapping("/uploadPdf")
    public String uploadPdf(@RequestParam("title") String title,
            @RequestParam("pdfFile") MultipartFile file,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return "redirect:/login";
        }

        try {
            final long MAX_SIZE = 20 * 1024 * 1024;

            if (file.getSize() > MAX_SIZE) {
                redirectAttributes.addFlashAttribute("error", "File size exceeds 20MB limit.");
                return "redirect:/upload_pdf";
            }

            LocalDate date = LocalDate.now();

            pdfService.savePdf(title, file, date, username);

            redirectAttributes.addAttribute("success", "Pdf saved successfully!");

            return "redirect:/upload_pdf";
        } catch (IOException e) {
            redirectAttributes.addAttribute("error", "Error uploading file ");
            return "redirect:/upload_pdf";
        }

    }

    @GetMapping("/admin_pdf_display")
    public String display_pdf_Admin(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<Pdf> pdfs = pdfService.display_Pdf_Admin();
            model.addAttribute("pdfs", pdfs);
            return "admin/admin_pdf";
        } else {
            return "redirect:/login"; // redirect to login if user is not logged in
        }
    }

    @GetMapping("/pdf_delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deletePdf(@PathVariable("id") Long id, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            try {
                pdfService.deletePdf(id);
                return ResponseEntity.ok("{\"message\": \"Pdf deleted successfully!\"}");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Failed to delete Pdf.\"}");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not authorized.\"}");
        }
    }

    @PostMapping("/edit_admin")
    @ResponseBody
    public ResponseEntity<String> edit_admin(@RequestBody EditCreatorRequestDTO editRequestDTO,
            HttpSession session) {
        String adminName = (String) session.getAttribute("loggedInUser");
        try {
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            // Optional<GCUser> optionalAdmin = gcUserRepository.findByUsername(adminName);
            Optional<GCUser> optionalAdmin = gcUserService.findByUsername(adminName);
            if (!optionalAdmin.isPresent()) {
                return ResponseEntity.status(404).body("Admin user not found.");
            }

            String existingAdminPassword = optionalAdmin.get().getPassword();
            String rawAdminPassword = editRequestDTO.getAdminPassword();

            if (!gcUserService.matches(rawAdminPassword, existingAdminPassword)) {
                return ResponseEntity.badRequest().body("Wrong Admin password");
            }

            if (!editRequestDTO.getNewUserPassword().equals(editRequestDTO.getUserConfirmPassword())) {
                return ResponseEntity.badRequest().body("Password and Confirm Password don't match.");
            }

            if (editRequestDTO.getNewUserPassword().equals(editRequestDTO.getUserConfirmPassword())) {

                ResponseEntity<String> message = gcUserService.editCreator(
                        editRequestDTO.getUserName(),
                        editRequestDTO.getNewUserName(),
                        editRequestDTO.getExistingUserPassword(),
                        editRequestDTO.getNewUserPassword(),
                        editRequestDTO.getUserConfirmPassword(),
                        LocalDate.now());

                return (message);
            } else {

                return ResponseEntity.badRequest().body(" Password and Confirm Password don't match");
            }

        } catch (Exception e) {
            log.error("Error occurred while editing admin ", e);
            // return ResponseEntity.status(500).body("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong. Please try again.");
        }
    }

}
