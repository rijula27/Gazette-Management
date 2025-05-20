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
import spring.aop.gazettemanagementnic.dto.TenderWithSize;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.service.TenderService;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@Controller
@RequestMapping("/tender")
public class TenderController {

    @Autowired
    public TenderService tenderService;




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
            // === Validate Tender fields ===
            if (tender.getTitle() == null || tender.getTitle().trim().isEmpty()
                || tender.getRef_No() == null || tender.getRef_No().trim().isEmpty()
                || tender.getAnnouncement_Date() == null
                || tender.getLast_Date() == null
                || tender.getOpening_Date() == null) {
                return ResponseEntity.badRequest()
                        .body("{\"error\": \"All required tender fields must be filled out.\"}");
            }
    
            // === Validate File ===
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

            // === Check for Duplicate Tender ===
            if (tenderService.isTenderExist(tender.getTitle(), tender.getRef_No())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("{\"error\": \"A tender with the same title and reference number already exists.\"}");
            }
    
            // === Save Tender ===
            tenderService.save_tender(
                username,
                tender.getTitle(),
                tender.getRef_No(),
                tender.getAnnouncement_Date(),
                tender.getLast_Date(),
                tender.getOpening_Date(),
                pdfFile,
                tender.getKeywords()
            );
    
            return ResponseEntity.ok("{\"message\": \"Tender uploaded successfully.\"}");
    
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred while processing the file.\"}");
        }
    }
    

    


    @GetMapping("/pdf/{id}")
    public ResponseEntity<?> viewTenderPdf(@PathVariable Long id) throws IOException {
        return tenderService.getTenderPdfResponse(id);
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

        System.out.println("tender path : "+ tenderPath);

        String filePath = tenderPath +year + "\\"+ tender.getTitle() + ".pdf"; // update path logic
        System.out.println("File Path: " + filePath);

        String size = "Unknown";
        
        try {
            Path path = Paths.get(filePath);
            long bytes = Files.size(path);
            double mb = bytes / (1024.0 * 1024.0);  // Convert to MB
            size = String.format("%.2f MB", mb);
        } catch (IOException e) {
            e.printStackTrace();
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
        List<Integer> monthNumbers =  tenderService.getAvailableMonths(year);
        return monthNumbers.stream()
        .map(month -> Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
        .collect(Collectors.toList());
    }


    @GetMapping("/years/{year}/months/{month}")
    @ResponseBody
    public List<Tender> getTendersByDate(@PathVariable Integer year, @PathVariable Integer month ) {
            return tenderService.getTendersByDate(year, month);
    }


}
