package spring.aop.gazettemanagementnic.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.dto.TenderWithSize;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.service.GCUserService;
import spring.aop.gazettemanagementnic.service.TenderService;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Controller
@RequestMapping("/tender")
public class TenderController {

    @Autowired
    public TenderService tenderService;

    @Autowired
    private GCUserService gcUserService;

    @PostMapping(value = "/uploadTender", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadTender(
            @RequestPart("tender") Tender tender,
            @RequestPart("pdfFile") MultipartFile pdfFile,
            HttpSession session) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not authorized. Please log in.\"}");
        }

        try {
            if (tender.getTitle() == null || tender.getTitle().trim().isEmpty()
                    || tender.getRef_No() == null || tender.getRef_No().trim().isEmpty()
                    || tender.getAnnouncement_Date() == null
                    || tender.getLast_Date() == null
                    || tender.getOpening_Date() == null) {
                        log.info("missing required fields: title={}, ref_No={}, announcement_Date={}, last_Date={}, opening_Date={}",
                                tender.getTitle(), tender.getRef_No(), tender.getAnnouncement_Date(),
                                tender.getLast_Date(), tender.getOpening_Date());   
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"All required tender fields must be filled out.\"}");
            }

            if (pdfFile == null || pdfFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"Please upload a valid PDF file.\"}");
            }

            // Check file size
            final long MAX_SIZE = 20 * 1024 * 1024; // 20MB
            if (pdfFile.getSize() > MAX_SIZE) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body("{\"error\": \"File size exceeds the 20MB limit.\"}");
            }

            // Check file type
            String contentType = pdfFile.getContentType();
            if (!"application/pdf".equalsIgnoreCase(contentType)) {
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"Only PDF files are allowed.\"}");
            }

            if (tenderService.isTenderExist(tender.getTitle(), tender.getRef_No())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"error\": \"A tender with the same title and reference number already exists.\"}");
            }

            tenderService.save_tender(
                    username,
                    tender.getTitle(),
                    tender.getRef_No(),
                    tender.getAnnouncement_Date(),
                    tender.getLast_Date(),
                    tender.getOpening_Date(),
                    pdfFile,
                    tender.getKeywords());

            return ResponseEntity.ok("{\"message\": \"Tender uploaded successfully.\"}");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the file.\"}");
        }
    }

    // @GetMapping("/pdf/{id}")
    // public ResponseEntity<?> viewTenderPdf(@PathVariable Long id, HttpSession
    // session) throws IOException {
    // // Check if user is authenticated
    // String username = (String) session.getAttribute("loggedInUser");
    // if (username == null) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    // .body("User not authenticated. Please login to view this resource.");
    // }

    // // Get user's role from database
    // java.util.Optional<GCUser> userOpt =
    // gcUserRepository.findByUsername(username);
    // if (!userOpt.isPresent()) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    // .body("User not found.");
    // }
    // String role = userOpt.get().getRole();

    // // Get tender by ID
    // java.util.Optional<Tender> tenderOpt = tenderRepository.findById(id);
    // if (!tenderOpt.isPresent()) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND)
    // .body("Tender not found.");
    // }
    // Tender tender = tenderOpt.get();

    // // Authorization rules based on dashboard visibility:
    // // - CREATOR/ADMIN can view only their own tenders they created
    // // - PUBLISHER (single role) can view ALL tenders sent by creators
    // if (role.equals("CREATOR") || role.equals("ADMIN")) {
    // // Creator/Admin can only view their own tenders
    // if (!tender.getGcUser().getUsername().equals(username)) {
    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body("You do not have permission to access this tender.");
    // }
    // return tenderService.getTenderPdfResponse(id);
    // } else if (role.equals("PUBLISHER")) {
    // // Publisher can view all tenders from creators
    // return tenderService.getTenderPdfResponse(id);
    // }

    // return ResponseEntity.status(HttpStatus.FORBIDDEN)
    // .body("You do not have permission to access this resource.");
    // }

    @GetMapping("/pdf/{id}")
    public ResponseEntity<?> viewTenderPdf(@PathVariable Long id, HttpSession session) throws IOException {

        // java.util.Optional<Tender> tenderOpt = tenderRepository.findById(id);
        Optional<Tender> tenderOpt = tenderService.findTenderById(id);
        if (!tenderOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tender not found.");
        }
        Tender tender = tenderOpt.get();

        String status = String.valueOf(tender.getStatus().getStatusCode());

        if ("3".equals(status)) {
            return tenderService.getTenderPdfResponse(id);
        }

        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated. Please login to view this resource.");
        }

        // Get user's role from database
        // java.util.Optional<GCUser> userOpt =
        // gcUserRepository.findByUsername(username);
        Optional<GCUser> userOpt = gcUserService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found.");
        }
        String role = userOpt.get().getRole();

        // if ("ADMIN".equals(role)) {
        // return tenderService.getTenderPdfResponse(id);
        // }

        // CREATOR: Can view their own drafts (status "1")
        if ("CREATOR".equals(role)) {
            if ("1".equals(status)) {
                // Creator can only view their own drafts
                if (tender.getGcUser().getUsername().equals(username)) {
                    return tenderService.getTenderPdfResponse(id);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You do not have permission to access this tender. This is not your draft.");
                }
            }
            // If not draft, creator cannot view other tenders
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to access this tender.");
        }

        // PUBLISHER: Can view tenders sent to them (status "2")
        if ("PUBLISHER".equals(role)) {
            if ("2".equals(status)) {
                return tenderService.getTenderPdfResponse(id);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to access this tender. Only pending tenders can be viewed by publishers.");
        }

        // Default: Deny access for unknown roles
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have permission to access this resource. Unknown user role.");
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editTender(
            @RequestParam("tenderId") Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "pdfFile", required = false) MultipartFile file,
            @RequestParam("opening_Date") LocalDate opening_Date,
            @RequestParam("last_Date") LocalDate last_Date,
            HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not authorized. Please log in.\"}");
        }
        try {
            // Validate file size if a file is uploaded
            if (file != null && !file.isEmpty()) {
                final long MAX_SIZE = 20 * 1024 * 1024; // 20MB in bytes
                if (file.getSize() > MAX_SIZE) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("File exceeded max size limit");
                }
            }
            tenderService.updateTender(id, title, username, last_Date, opening_Date, file);
            return ResponseEntity.ok("Tender updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update tender: " + e.getMessage());
        }
    }

    @GetMapping("/display")
    public String showTenderPage(Model model) {
        List<Tender> tenderList = tenderService.getActiveTenders();
        List<TenderWithSize> tenders = new ArrayList<>();

        for (Tender tender : tenderList) {

            String tenderPath = tender.getFilePath().getFullPath();
            int year = tender.getAnnouncement_Date().getYear();

            String filePath = tenderPath + year + "\\" + tender.getTitle() + ".pdf"; // update path logic

            String size = "Unknown";

            try {
                Path path = Paths.get(filePath);
                long bytes = Files.size(path);
                double mb = bytes / (1024.0 * 1024.0); // Convert to MB
                size = String.format("%.2f MB", mb);
            } catch (IOException e) {
                log.error("Error occurred while display tender: {}", e.getMessage());

            }

            tenders.add(new TenderWithSize(tender, size));
        }

        model.addAttribute("tendersWithSize", tenders);
        return "tender";
    }

    @GetMapping("/displayArchive")
    public String showTenderArchivePage(Model model) {
        List<Integer> years = tenderService.getAvailableYears();
        model.addAttribute("years", years);
        return "tenderArchive";
    }

    @GetMapping("/years/{year}/months")
    @ResponseBody
    public List<String> getMonthsByYear(@PathVariable Integer year) {
        List<Integer> monthNumbers = tenderService.getAvailableMonths(year);
        return monthNumbers.stream()
                .map(month -> Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .collect(Collectors.toList());
    }

    @GetMapping("/years/{year}/months/{month}")
    @ResponseBody
    public List<Tender> getTendersByDate(@PathVariable Integer year, @PathVariable Integer month) {
        return tenderService.getTendersByDate(year, month);
    }

}
