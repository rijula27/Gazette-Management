package spring.aop.gazettemanagementnic.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.MediaType;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.ImageGallery;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.ImageGalleryRepository;

@Slf4j
@Service
@AllArgsConstructor
public class ImageGalleryService {

    @Autowired
    private GCUserRepository gcUserRepository;

    @Autowired
    private FilePathRepository filePathRepository;

    @Autowired
    private ImageGalleryRepository imageGalleryRepository;

    private final FilePathService filePathService;

    public ResponseEntity<?> saveImage(MultipartFile image, String description, String adminName) throws IOException {

        GCUser gcUser = gcUserRepository.findByUsername(adminName)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));

        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        final long MAX_SIZE = 5 * 1024 * 1024;
        if (image.getSize() > MAX_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File size exceeds limit");
        }

        // ✅ Validate filename
        String originalFilename = image.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            log.info("invalid file name : " + originalFilename);
            throw new IllegalArgumentException("Invalid file name");
        }

        String cleanFilename = new File(originalFilename).getName();

        if (!cleanFilename.matches("^[a-zA-Z0-9._ -]+$")) {

            log.warn("invalid file name : {}", cleanFilename);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid file name");
        }

        // ✅ Validate file type
        String contentType = image.getContentType();
        if (contentType == null ||
                !(contentType.equals("image/jpeg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/jpg"))) {
            log.info("invalid file type : " + contentType);

            throw new IllegalArgumentException("Only image files are allowed");
        }

        // ✅ Generate safe filename (BEST PRACTICE)
        // String extension = cleanFilename.contains(".")
        // ? cleanFilename.substring(cleanFilename.lastIndexOf("."))
        // : "";

        String extension;

        switch (contentType) {
            case "image/jpeg":
            case "image/jpg":
                extension = ".jpg";
                break;
            case "image/png":
                extension = ".png";
                break;
            default:
                throw new IllegalArgumentException("Unsupported file type");
        }

        String uniqueFilename = UUID.randomUUID().toString() + extension;

        log.info("Generated unique filename: " + uniqueFilename);

        // ✅ Get upload directory safely
        FilePath filePathEntity = filePathRepository
                .findByPathDescription("Gallery Local Path")
                .orElseThrow(() -> new IllegalArgumentException("Upload path not configured"));

        String uploadDir = filePathEntity.getFullPath();

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // ✅ Create directory if not exists
        if (!uploadPath.toFile().exists()) {
            uploadPath.toFile().mkdirs();
        }

        // ✅ Secure path resolution (CRITICAL FIX)
        Path resolvedPath = uploadPath.resolve(uniqueFilename).normalize();

        if (!resolvedPath.startsWith(uploadPath)) {
            throw new SecurityException("Invalid file path");
        }

        // ✅ Save file
        image.transferTo(resolvedPath.toFile());

        if (description != null) {

            description = description.trim();

            // Maximum length
            if (description.length() > 500) {
                throw new IllegalArgumentException("Description cannot exceed 500 characters.");
            }

            // Reject HTML/XML tags
            if (description.matches(".*<[^>]+>.*")) {
                throw new IllegalArgumentException("HTML or script tags are not allowed in the description.");
            }

            // Allow only expected characters
            String descriptionPattern = "^[A-Za-z0-9 .,'()&/_\\-:;!?@#%\\n\\r]*$";

            if (!description.matches(descriptionPattern)) {
                throw new IllegalArgumentException("Description contains invalid characters.");
            }
        }
        // ✅ Save to DB
        ImageGallery imageGallery = new ImageGallery();
        imageGallery.setImageTitle(uniqueFilename);
        imageGallery.setDescription(description);
        imageGallery.setFilePath(filePathEntity);
        imageGallery.setGcUser(gcUser);
        imageGallery.setUploadDate(LocalDate.now());

        imageGalleryRepository.save(imageGallery);

        return ResponseEntity.ok().body("File uploaded successfully");
    }

    public List<ImageGallery> displayImage() {

        return imageGalleryRepository.findAll();
    }

    public ResponseEntity<Resource> getImageById(Long imageId) throws IOException {

        // Fetch base path
        Optional<FilePath> optionalPath = filePathService.getFilePathByDescription("Gallery Local Path");
        if (optionalPath.isEmpty()) {
            log.warn("Gallery Local Path not found in file_path table");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String uploadDir = optionalPath.get().getFullPath();

        // Fetch image from DB
        Optional<ImageGallery> optionalImage = imageGalleryRepository.findByImageId(imageId);
        if (optionalImage.isEmpty()) {
            log.warn("Image not found for ID: {}", imageId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        String imageName = optionalImage.get().getImageTitle();

        // Validate filename (defense-in-depth)
        if (!imageName.matches("^[a-zA-Z0-9._ -]+$")) {
            return ResponseEntity.badRequest().build();
        }

        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path imagePath = basePath.resolve(imageName).normalize();

        // Prevent path traversal
        if (!imagePath.startsWith(basePath)) {
            log.warn("Path traversal attempt blocked for image: {}", imageName);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Check file
        if (!Files.exists(imagePath) || !Files.isReadable(imagePath) || !Files.isRegularFile(imagePath)) {
            log.warn("Image file not found or not readable: {}", imagePath);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Detect MIME
        String contentType = Files.probeContentType(imagePath);
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }

        Resource resource = new UrlResource(imagePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("X-Content-Type-Options", "nosniff")
                .header("Content-Disposition", "inline; filename=\"" + imageName + "\"")
                .body(resource);
    }

}
