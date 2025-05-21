package spring.aop.gazettemanagementnic.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import spring.aop.gazettemanagementnic.entity.ContactUs;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.ImageGallery;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.ImageGalleryRepository;
import spring.aop.gazettemanagementnic.service.ImageGalleryService;

@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

    @Autowired
    private ImageGalleryService imageGalleryService;


    @Autowired
    private FilePathRepository filePathRepository;

    @Autowired
    private ImageGalleryRepository imageGalleryRepository;
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam("description") String description,
            HttpSession session) {

        String adminName = (String) session.getAttribute("loggedInUser");

        if (adminName == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized: Please log in.");
        }

        try {
            final long MAX_SIZE = 5 * 1024 * 1024; // 5MB

            if (image.getSize() > MAX_SIZE) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "error");
                response.put("message", "File size exceeds the 5MB limit.");
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
            }

            imageGalleryService.saveImage(image, description, adminName); // update service to accept adminName
            return ResponseEntity.ok().body("Image uploaded successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        }
    }



    @GetMapping("/imageDisplay")
    public String imageGallery_Display(Model model, HttpSession session) {

        String username = (String) session.getAttribute("loggedInUser");
        if (username != null) {
            List<ImageGallery> image = imageGalleryService.displayImage();
            model.addAttribute("images",image);
            return "admin/admin_gallery";   
        }else{
            return "redirect:/login"; 
        }
    }


    @GetMapping("/images/{imageName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String imageName) throws MalformedURLException {
        Optional<FilePath> optionalPath = filePathRepository.findByPathDescription("Gallery Local Path");
        if (optionalPath.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        String uploadDir = optionalPath.get().getFullPath();
        Path imagePath = Paths.get(uploadDir + imageName);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(imagePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // You can determine MIME type dynamically
                .body(resource);
    }




    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        Optional<ImageGallery> optionalImage = imageGalleryRepository.findById(id);
    
        if (optionalImage.isPresent()) {
            ImageGallery image = optionalImage.get();
        
            // Optionally delete the file from the local path
            String filePath = image.getFilePath().getFullPath() + image.getImageTitle();
            File file = new File(filePath);
        
            if (file.exists()) {
                file.delete(); // Remove file from filesystem
            }
        
            imageGalleryRepository.deleteById(id);
        
            return ResponseEntity.ok("Image deleted successfully.");
        }
    
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                             .body("Image not found with ID: " + id);
    }


}
