package spring.aop.gazettemanagementnic.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.ImageGallery;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.ImageGalleryRepository;

@Slf4j
@Service
public class ImageGalleryService {

    @Autowired
    private GCUserRepository gcUserRepository;

    @Autowired
    private FilePathRepository filePathRepository;

    @Autowired
    private ImageGalleryRepository imageGalleryRepository;

    public ResponseEntity<?> saveImage(MultipartFile image, String description, String adminName) throws IOException {

        GCUser gcUser = gcUserRepository.findByUsername(adminName)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));

        // ✅ Validate filename
        String originalFilename = image.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            log.info("invalid file name : " + originalFilename);
            throw new IllegalArgumentException("Invalid file name");
        }

        String cleanFilename = new File(originalFilename).getName();

        if (!cleanFilename.matches("^[a-zA-Z0-9._ -]+$")) {

            log.info("invalid file name : " + cleanFilename);

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
        String extension = cleanFilename.contains(".")
                ? cleanFilename.substring(cleanFilename.lastIndexOf("."))
                : "";

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

}
