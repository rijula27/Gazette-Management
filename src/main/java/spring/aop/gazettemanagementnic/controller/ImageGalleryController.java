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
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.ImageGallery;
import spring.aop.gazettemanagementnic.repository.ImageGalleryRepository;
import spring.aop.gazettemanagementnic.service.FilePathService;
import spring.aop.gazettemanagementnic.service.ImageGalleryService;

@Slf4j
@Controller
@RequestMapping("/gallery")
public class ImageGalleryController {

    @Autowired
    private ImageGalleryService imageGalleryService;

    @Autowired
    private ImageGalleryRepository imageGalleryRepository;

    @Autowired
    private FilePathService filePathService;

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

            return imageGalleryService.saveImage(image, description, adminName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image.");
        }
    }

    @GetMapping("/imageDisplay")
    public String imageGallery_Display(Model model, HttpSession session) {

        log.info("=== ENTERED imageDisplay ===");

        String username = (String) session.getAttribute("loggedInUser");
        log.info("Session username = {}", username);

        if (username != null) {
            log.info("Before service call");

            List<ImageGallery> image = imageGalleryService.displayImage();

            log.info("After service call");
            log.info("image object = {}", image);
            log.info("image size = {}", (image != null ? image.size() : "NULL"));

            if (image != null && !image.isEmpty()) {
                for (ImageGallery img : image) {
                    log.info("Image ID: {}", img.getImageId());
                    log.info("Title: {}", img.getImageTitle());
                    log.info("Description: {}", img.getDescription());
                }
            } else {
                log.info("Image list is empty or null");
            }

            model.addAttribute("images", image);
            return "admin/admin_gallery";
        } else {
            log.info("No session user, redirecting login");
            return "redirect:/login";
        }
    }

    @GetMapping("/images/{imageName:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveImage(@PathVariable String imageName) throws MalformedURLException {

        try {
            log.info("Entered serveImage() with imageName: {}", imageName);

            // Optional<FilePath> optionalPath =
            // filePathRepository.findByPathDescription("Gallery Local Path");
            Optional<FilePath> optionalPath = filePathService.getFilePathByDescription("Gallery Local Path");
            if (optionalPath.isEmpty()) {
                log.warn("Gallery Local Path not found in file_path table");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String uploadDir = optionalPath.get().getFullPath();
            log.info("Upload directory fetched from DB: {}", uploadDir);

            if (imageName == null || imageName.trim().isEmpty()) {
                log.warn("Image name is null or empty");
                return ResponseEntity.badRequest().build();
            }

            if (!imageName.matches("^[a-zA-Z0-9._-]+$")) {
                log.warn("Invalid image name format detected: {}", imageName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            String lowerFileName = imageName.toLowerCase();
            if (!(lowerFileName.endsWith(".jpg") ||
                    lowerFileName.endsWith(".jpeg") ||
                    lowerFileName.endsWith(".png") ||
                    lowerFileName.endsWith(".gif") ||
                    lowerFileName.endsWith(".webp"))) {
                log.warn("Unsupported file extension for image: {}", imageName);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path imagePath = basePath.resolve(imageName).normalize();

            log.info("Base path: {}", basePath);
            log.info("Resolved image path: {}", imagePath);

            // Prevent path traversal
            if (!imagePath.startsWith(basePath)) {
                log.warn("Path traversal attempt blocked for image: {}", imageName);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Check file existence
            if (!Files.exists(imagePath) || !Files.isReadable(imagePath) || !Files.isRegularFile(imagePath)) {
                log.warn("Image file not found or not readable: {}", imagePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Detect MIME type dynamically
            String contentType = Files.probeContentType(imagePath);
            log.info("Detected content type for {}: {}", imageName, contentType);

            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Invalid content type for image {}: {}", imageName, contentType);
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
            }

            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                log.warn("Resource exists but not readable: {}", imagePath);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("Successfully serving image: {}", imagePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header("X-Content-Type-Options", "nosniff")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error while serving image: {}", imageName, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteImage(@PathVariable Long id) {
        try {
            Optional<ImageGallery> optionalImage = imageGalleryRepository.findById(id);

            if (optionalImage.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Image not found.");
            }

            ImageGallery image = optionalImage.get();

            // Secure path handling
            Path basePath = Paths.get(image.getFilePath().getFullPath()).toAbsolutePath().normalize();
            Path filePath = basePath.resolve(image.getImageTitle()).normalize();

            // Prevent path traversal
            if (!filePath.startsWith(basePath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Invalid file path.");
            }

            File file = filePath.toFile();

            if (file.exists() && file.isFile()) {
                file.delete();
            }

            imageGalleryRepository.deleteById(id);

            return ResponseEntity.ok("Image deleted successfully.");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting image.");
        }
    }

}
