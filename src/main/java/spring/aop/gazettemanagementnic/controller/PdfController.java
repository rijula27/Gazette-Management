package spring.aop.gazettemanagementnic.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import spring.aop.gazettemanagementnic.dto.PdfWithSize;
import spring.aop.gazettemanagementnic.entity.ImageGallery;
import spring.aop.gazettemanagementnic.entity.Pdf;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.repository.PdfRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.service.ImageGalleryService;
import spring.aop.gazettemanagementnic.service.PdfService;


@Controller
public class PdfController {
    

    @Autowired
    PdfService pdfService;

    @Autowired
    ImageGalleryService imageGalleryService;

    @Autowired
    private PdfRepository pdfRepository;

    @Autowired
    private GCUserRepository gcUserRepository;
    

    // @GetMapping("/index")
    //     public String indexPage(Model model) {
    //     List<Pdf> importantPdfs = pdfService.getImportantPdfs();

    //     List<ImageGallery> images = imageGalleryService.displayImage();

    //     model.addAttribute("importantPdfs", importantPdfs);
    //     model.addAttribute("images", images);
    //     return "index";
    // }


    @GetMapping({"/index","/","/home"})
    public String indexPage(Model model) {
        List<Pdf> importantPdfs = pdfService.getImportantPdfs();
        List<PdfWithSize> pdfsWithSize = new ArrayList<>();
    
        for (Pdf pdf : importantPdfs) {
            String pdfPath = pdf.getFilePath().getFullPath(); // assuming you have getFilePath().getFullPath()
            String filePath = pdfPath + pdf.getPdfTitle() + ".pdf"; // Adjust as needed
        
            String size = "Unknown";
            try {
                Path path = Paths.get(filePath);
                long bytes = Files.size(path);
                double mb = bytes / (1024.0 * 1024.0);
                size = String.format("%.2f MB", mb);
            } catch (IOException e) {
                e.printStackTrace();
            }
        
            pdfsWithSize.add(new PdfWithSize(pdf, size));
        }
    
        List<ImageGallery> images = imageGalleryService.displayImage();
    
        model.addAttribute("importantPdfs", pdfsWithSize); // updated list
        model.addAttribute("images", images);
        return "index";
    }


   
    // @GetMapping("/pdf/{id}")
    // public ResponseEntity<?> viewPdf(@PathVariable Long id, HttpSession session) throws IOException {
    //     // Check if user is authenticated
    //     String username = (String) session.getAttribute("loggedInUser");
    //     if (username == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //             .body("User not authenticated. Please login to view this resource.");
    //     }
        
    //     // Get user's role from database
    //     java.util.Optional<GCUser> userOpt = gcUserRepository.findByUsername(username);
    //     if (!userOpt.isPresent()) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
    //             .body("User not found.");
    //     }
    //     String role = userOpt.get().getRole();
        
    //     // Get PDF by ID
    //     java.util.Optional<Pdf> pdfOpt = pdfRepository.findById(id);
    //     if (!pdfOpt.isPresent()) {
    //         return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //             .body("PDF not found.");
    //     }
    //     Pdf pdf = pdfOpt.get();
        
    //     // Authorization rules:
    //     // - CREATOR can only access PDFs they created
    //     // - ADMIN can only access PDFs they created
    //     // - PUBLISHER can only access PDFs marked for them
    //     if (role.equals("CREATOR") || role.equals("ADMIN")) {
    //         // Creator/Admin can only view their own PDFs
    //         if (!pdf.getGcUser().getUsername().equals(username)) {
    //             return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                 .body("You do not have permission to access this PDF.");
    //         }
    //         return pdfService.getPdfResponse(id);
    //     } else if (role.equals("PUBLISHER")) {
    //         // Publisher can only view PDFs they have access to
    //         // For now, restrict to owner or check if marked for publisher
    //         if (!pdf.getGcUser().getUsername().equals(username)) {
    //             return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //                 .body("You do not have permission to access this PDF.");
    //         }
    //         return pdfService.getPdfResponse(id);
    //     }
        
    //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
    //         .body("You do not have permission to access this resource.");
    // }
    

    @GetMapping("/pdf/{id}")
    public ResponseEntity<?> viewPdf(@PathVariable Long id) throws IOException{
        return pdfService.getPdfResponse(id);
    }



     @GetMapping("/siteMap")
public String siteMap(Model model) {
    List<Pdf> importantPdfs = pdfService.getImportantPdfs();
    model.addAttribute("pdfs", importantPdfs);
    return "siteMap";
}

}
