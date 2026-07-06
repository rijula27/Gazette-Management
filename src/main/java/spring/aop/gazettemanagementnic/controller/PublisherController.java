package spring.aop.gazettemanagementnic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.service.GazetteService;
import spring.aop.gazettemanagementnic.service.TenderService;

@Slf4j
@Controller
@RequestMapping("/publisher")
public class PublisherController {

    @Autowired
    private GazetteService gazetteService;

    @Autowired
    private TenderService tenderService;

    @GetMapping("/publisher_display")
    public String displayGazettePublisher(Model model) {
        
            List<Gazette> gazettes = gazetteService.displayGazette_Publisher();
            model.addAttribute("gazettes", gazettes);
            return "publisher/publisher";
        

    }

    @GetMapping("/publisher_delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteGazette(@PathVariable("id") Long id) {
        

        try {
            gazetteService.deleteGazette(id);
            return ResponseEntity.ok("{\"message\": \"Gazette deleted successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to delete gazette. Please try again later.\"}");
        }
    }


    @GetMapping("/tender_delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTender(@PathVariable("id") Long id) {
        
            try {

                log.info("Attempting to delete tender with ID: " + id);
                tenderService.deleteTender(id);
                return ResponseEntity.ok("{\"message\": \"Tender deleted successfully!\"}");
            } catch (Exception e) {
                log.error("Error occurred while deleting tender with ID: " + id, e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Failed to delete Tender.\"}");
            }
        
    }

    @GetMapping("/sendBack_Creator/{id}")
    @ResponseBody
    public ResponseEntity<?> sendBackCreator(@PathVariable("id") Long id) {
        
        try {
            gazetteService.send_Back_Creator(id);
            return ResponseEntity.ok("{\"message\": \"Gazette send successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to send gazette.\"}");
        }
    }

    @GetMapping("/published/{id}")
    @ResponseBody
    public ResponseEntity<?> published(@PathVariable("id") Long id) {
        
        try {
            gazetteService.published(id);
            return ResponseEntity.ok("{\"message\": \"Gazette published successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to published gazette.\"}");
        }
    }

    @GetMapping("/publisher_submission_history")
    public String publisherSubmissionHistory(Model model) {
        

        // Fetch user's gazettes
        List<Gazette> gazettes = gazetteService.displayPublisherAllGazette();

        // Calculate stats
        int totalSubmissions = gazettes.size();
        int approved = 0;
        int sendBack = 0;

        List<Gazette> approved_Gazettes = gazetteService.display_published_Gazette();

        List<Gazette> send_back_Gazettes = gazetteService.display_send_back_Gazette();

        approved = approved_Gazettes.size();

        sendBack = send_back_Gazettes.size();

        // Add data to model
        model.addAttribute("gazettes", gazettes);
        model.addAttribute("totalSubmissions", totalSubmissions);
        model.addAttribute("approved", approved);
        model.addAttribute("send", sendBack);

        return "publisher/publisher_submission_history"; // This is your HTML/Thymeleaf view
    }

    @GetMapping("/publisher_tender_display")
    public String display_Tender_Publisher(Model model) {

        

        List<Tender> tenders = tenderService.display_Tender_Publisher();
        model.addAttribute("tenders", tenders);
        return "publisher/publisher_tender";
    }

    @GetMapping("/sendBack_tender_Creator/{id}")
    @ResponseBody
    public ResponseEntity<?> sendBackTenderCreator(@PathVariable("id") Long id) {
        
        try {
            tenderService.send_Back_tender_Creator(id);
            return ResponseEntity.ok("{\"message\": \"Gazette send successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to send gazette.\"}");
        }
    }

    @GetMapping("/published_tender/{id}")
    @ResponseBody
    public ResponseEntity<?> publishedTender(@PathVariable("id") Long id) {
        
        try {
            tenderService.published_tender(id);
            return ResponseEntity.ok("{\"message\": \"Gazette published successfully!\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to published gazette.\"}");
        }
    }

    @GetMapping("/publisher_tender_submission_history")
    public String publisherTenderSubmissionHistory(Model model) {
        

        // Fetch user's gazettes
        List<Tender> tenders = tenderService.displayPublisherAllTender();

        // Calculate stats
        int totalSubmissions = tenders.size();
        int approved = 0;
        int sendBack = 0;

        List<Tender> approved_Tenders = tenderService.display_published_Tender();

        List<Tender> send_back_Tenders = tenderService.display_send_back_Tender();

        approved = approved_Tenders.size();

        sendBack = send_back_Tenders.size();

        // Add data to model
        model.addAttribute("tenders", tenders);
        model.addAttribute("totalSubmissions", totalSubmissions);
        model.addAttribute("approved", approved);
        model.addAttribute("send", sendBack);

        return "publisher/publisher_tender_submission_history";
    }

}
