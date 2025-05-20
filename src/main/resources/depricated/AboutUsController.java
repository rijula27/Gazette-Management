package spring.aop.gazettemanagementnic.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import spring.aop.gazettemanagementnic.entity.AboutUs;
import spring.aop.gazettemanagementnic.entity.ContactUs;
import spring.aop.gazettemanagementnic.service.AboutUsService;

@Controller
@RequestMapping("/about")
public class AboutUsController {

    @Autowired
    private AboutUsService aboutUsService;

    @PostMapping("/save")
    @ResponseBody
    public ResponseEntity<String> saveAbout(@RequestBody AboutUs aboutUs,
                                                HttpSession session ) {
                            
        String adminName = (String) session.getAttribute("loggedInUser");

        try{
            if(adminName == null || adminName.isEmpty()){
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            String resultMessage = aboutUsService.saveAbout(
                aboutUs.getSectionHeading(),
                aboutUs.getSectionContent(),
                adminName,
                LocalDate.now()
            );
            return ResponseEntity.ok(resultMessage);


        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Something went wrong please try again");
        }

                                
    }



    @GetMapping("/aboutDisplay")
    public String aboutUs_Display(Model model, HttpSession session) {

        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<AboutUs> section = aboutUsService.displayContent();
            model.addAttribute("sections",section);
            return "admin/admin_aboutUs";   
        }else{
            return "redirect:/login"; 
        }
    }


    @GetMapping("/display")
        public String aboutPage(Model model) {
        List<AboutUs> section = aboutUsService.displayContent();
        model.addAttribute("sections", section);
        return "about";
    }



    
    @GetMapping("/delete/{id}")
    public String deleteContent(@PathVariable("id") Long id, Model model, HttpSession session){
        String adminName = (String) session.getAttribute("loggedInUser");
        if (adminName != null) {
            aboutUsService.deleteContent(id);
            model.addAttribute("successMessage", "User deleted successfully!");
            return "redirect:/about/aboutDisplay";
        }else{
            return "redirect:/login"; // redirect to login if user is not logged in

        }

    }


    
    @PostMapping("/edit")
    @ResponseBody
    public ResponseEntity<String> editContent(@RequestBody AboutUs aboutUs, HttpSession session) {
        
        String adminName = (String) session.getAttribute("loggedInUser");

        try{
            if (adminName == null || adminName.isEmpty()) {
                return ResponseEntity.status(401).body("Session expired or not logged in.");
            }

            String resultMessage = aboutUsService.editContent(aboutUs.getSectionId(),aboutUs.getSectionHeading(),
            aboutUs.getSectionContent(), adminName, LocalDate.now());
            return ResponseEntity.ok(resultMessage);
            
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Something went wrong. Please try again.");
        }

    }
    
}
