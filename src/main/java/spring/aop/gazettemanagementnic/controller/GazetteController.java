package spring.aop.gazettemanagementnic.controller;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.repository.GazetteRepository;
import spring.aop.gazettemanagementnic.service.GCUserService;
import spring.aop.gazettemanagementnic.service.GazetteService;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;



@Slf4j
@Controller
@RequestMapping("/gazette")
public class GazetteController {

    @Autowired
    private GazetteService gazetteService;

    @Autowired
    private GazetteRepository gazetteRepository;

    @Autowired
    private GCUserService gcUserService;

    @PostMapping("/upload")
    public String uploadGazette(@RequestParam("gazettePart") String part,
            @RequestParam("pdfFile") MultipartFile file,
            @RequestParam(value = "date", required = false) LocalDate date,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return "redirect:/login";
        }

        try {
            gazetteService.saveGazette(part, file, date, username);
            redirectAttributes.addFlashAttribute("success", "Gazette saved successfully!");
            return "redirect:/creator";

        } catch (IllegalArgumentException e) {
            log.error("Validation error in gazette upload: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/creator";

        } catch (FileAlreadyExistsException e) {
            log.error("File already exists: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Gazette already exists for this date and part");
            return "redirect:/creator";

        } catch (Exception e) {
            log.error("Unexpected error in gazette upload: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "An error occurred. Please try again.");
            return "redirect:/creator";
        }
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editGazette(
            @RequestParam("gazetteId") Long id,
            @RequestParam("part") String part,
            @RequestParam(value = "pdfFile", required = false) MultipartFile file,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session) {

        String username = (String) session.getAttribute("loggedInUser");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not authorized.\"}");
        }

        try {
            // Validate file if it's not null
            if (file != null && !file.isEmpty()) {
                final long MAX_SIZE = 20 * 1024 * 1024; // 20MB
                if (file.getSize() > MAX_SIZE) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("{\"error\": \"File size exceeds 20MB limit.\"}");
                }
            }

            gazetteService.updateGazette(id, part, file, username, date);
            return ResponseEntity.ok("{\"message\": \"Gazette updated successfully!\"}");

        } catch (IllegalArgumentException e) {
            log.error("Validation error in gazette edit: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");

        } catch (Exception e) {
            // ✅ Generic message only — no details leaked
            log.error("Unexpected error in gazette edit: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"An error occurred. Please try again.\"}");
        }
    }

    // view pdf
    @GetMapping("/pdf/{id}")
    public ResponseEntity<?> viewGazettePdf(@PathVariable Long id, HttpSession session) throws IOException {

        // Get gazette by ID first (public check, no auth needed yet)
        java.util.Optional<Gazette> gazetteOpt = gazetteRepository.findById(id);
        if (!gazetteOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Gazette not found.");
        }
        Gazette gazette = gazetteOpt.get();

        String status = String.valueOf(gazette.getStatus().getStatusCode());

        log.info("Gazette status code: {}", status);

        if ("3".equals(status)) {
            return gazetteService.getGazettePdfResponse(id);
        }

        String username = (String) session.getAttribute("loggedInUser");
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not authenticated. Please login to view this resource.");
        }

        // java.util.Optional<GCUser> userOpt =
        // gcUserRepository.findByUsername(username);
        Optional<GCUser> userOpt = gcUserService.findByUsername(username);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User not found.");
        }
        String role = userOpt.get().getRole();

        // if ("ADMIN".equals(role)) {
        // return gazetteService.getGazettePdfResponse(id);
        // }

        if ("CREATOR".equals(role)) {
            if ("1".equals(status)) {
                // Creator can only view their own drafts
                if (gazette.getGcUser().getUsername().equals(username)) {
                    return gazetteService.getGazettePdfResponse(id);
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("You do not have permission to access this gazette. This is not your draft.");
                }
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to accddgdess this gazette.");
        }

        if ("PUBLISHER".equals(role)) {
            if ("2".equals(status)) {
                return gazetteService.getGazettePdfResponse(id);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You do not have permission to access this gazette. Only pending gazettes can be viewed by publishers.");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("You do not have permission to access this resource. Unknown user role.");
    }

    @GetMapping("/display")
    public String showGazettePage(Model model) {
        List<Integer> years = gazetteService.getAvailableYears();
        model.addAttribute("years", years);
        return "gazette";
    }

    @GetMapping("/years/{year}/months")
    @ResponseBody
    public List<String> getMonthsByYear(@PathVariable Integer year) {
        List<Integer> monthNumbers = gazetteService.getAvailableMonths(year);
        return monthNumbers.stream()
                .map(month -> Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH))
                .collect(Collectors.toList());
    }

    @GetMapping("/years/{year}/months/{month}/dates")
    @ResponseBody
    public List<Integer> getDatesByYearAndMonth(@PathVariable Integer year, @PathVariable Integer month) {
        return gazetteService.getAvailableDates(year, month); // Should return List<Integer>
    }

    @GetMapping("/years/{year}/months/{month}/dates/{date}")
    @ResponseBody
    public List<Gazette> getGazettesByDate(@PathVariable Integer year, @PathVariable Integer month,
            @PathVariable Integer date) {
        return gazetteService.getGazettesByDate(year, month, date);
    }

}
