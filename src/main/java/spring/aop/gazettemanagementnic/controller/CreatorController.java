package spring.aop.gazettemanagementnic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.service.GazetteService;
import spring.aop.gazettemanagementnic.service.TenderService;

@Controller
@RequestMapping("/creator")
public class CreatorController {

    @Autowired
    private GazetteService gazetteService;


    @Autowired
    private TenderService tenderService;
    

    @GetMapping("/display")
    public String displayGazette(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        if(username != null){
            List<Gazette> gazettes = gazetteService.displayGazette(username);
            model.addAttribute("gazettes", gazettes);
            return "creator/creator_dashboard";
        }else{
            return "redirect:/login"; // redirect to login if user is not logged in
        }

    }


    @GetMapping("/sendPublisher/{id}")
    @ResponseBody
    public ResponseEntity<?> send_to_Publisher(@PathVariable("id") Long id, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if(username != null){
            try{
                gazetteService.send_to_Publisher(id);
                return ResponseEntity.ok("{\"message\": \"Gazette send successfully!\"}");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Failed to send gazette.\"}");
            }
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"error\": \"User not authorized.\"}");
        }
    }



    @GetMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteGazette(@PathVariable("id") Long id, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if(username != null){
            try {
                gazetteService.deleteGazette(id);
                return ResponseEntity.ok("{\"message\": \"Gazette deleted successfully!\"}");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Failed to delete gazette.\"}");
            }
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("{\"error\": \"User not authorized.\"}");
        }
    }



    
    @GetMapping("/submission_history")
    public String submissionHistory(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
    
        if (username != null) {
            // Fetch all categorized gazettes
            List<Gazette> gazettes = gazetteService.displayAllGazette(username);
            List<Gazette> approvedGazettes = gazetteService.display_approved_Gazette(username);
            List<Gazette> sendGazettes = gazetteService.display_send_Gazette(username);
            List<Gazette> createGazettes = gazetteService.display_create_Gazette(username);
    
            // Add data to model
            model.addAttribute("gazettes", gazettes);
            model.addAttribute("totalSubmissions", gazettes.size());
            model.addAttribute("approved", approvedGazettes.size());
            model.addAttribute("send", sendGazettes.size());
            model.addAttribute("create", createGazettes.size());
    
            return "creator/creator_submission_history";
        } else {
            return "redirect:/login";
        }
    }
    




    @GetMapping("/tender_display")
    public String displayTender(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
    
        if (username != null) {
            List<Tender> tenders = tenderService.displayTender(username);
            model.addAttribute("tenders", tenders);
            return "creator/creator_tender_dashboard";
        } else {
            return "redirect:/login";
        }
    }
    



    @GetMapping("/tender_delete/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTender(@PathVariable("id") Long id, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            try {
                tenderService.deleteTender(id);
                return ResponseEntity.ok("{\"message\": \"Tender deleted successfully!\"}");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("{\"error\": \"Failed to delete Tender.\"}");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not authorized.\"}");
        }
    }





    @GetMapping("/sendTenderPublisher/{id}")
    @ResponseBody
    public ResponseEntity<?> sendTenderPublisher(@PathVariable("id") Long id, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            try{
                tenderService.send_tender_Publisher(id);
                return ResponseEntity.ok("{\"message\": \"Tender send successfully!\"}");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Failed to send Tender.\"}");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"User not authorized.\"}");
        }
    }




    @GetMapping("/tender_submission_history")
    public String tenderSubmissionHistory(Model model, HttpSession session) {
        String username = (String) session.getAttribute("loggedInUser");
    
        if (username == null) {
            return "redirect:/login";
        }
    
        List<Tender> tenders = tenderService.displayAllTender(username);
        List<Tender> approvedTenders = tenderService.display_approved_Tender(username);
        List<Tender> sendTenders = tenderService.display_send_Tender(username);
        List<Tender> createTenders = tenderService.display_create_Tender(username);
    
        model.addAttribute("tenders", tenders);
        model.addAttribute("totalSubmissions", tenders.size());
        model.addAttribute("approved", approvedTenders.size());
        model.addAttribute("send", sendTenders.size());
        model.addAttribute("create", createTenders.size());
    
        return "creator/creator_tender_submission_history";
    }

   
}
