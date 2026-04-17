// package spring.aop.gazettemanagementnic.service;

// import java.io.File;
// import java.io.IOException;
// import java.nio.file.FileAlreadyExistsException;
// import java.time.LocalDate;
// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.core.io.Resource;
// import org.springframework.core.io.UrlResource;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import spring.aop.gazettemanagementnic.entity.FilePath;
// import spring.aop.gazettemanagementnic.entity.GCUser;
// import spring.aop.gazettemanagementnic.entity.Pdf;
// import spring.aop.gazettemanagementnic.repository.FilePathRepository;
// import spring.aop.gazettemanagementnic.repository.GCUserRepository;
// import spring.aop.gazettemanagementnic.repository.PdfRepository;

// @Service
// public class PdfService {

//     @Autowired
//     GCUserRepository gcUserRepository;

//     @Autowired
//     FilePathRepository filePathRepository;

//     @Autowired
//     PdfRepository pdfRepository;

//     public void savePdf(String title, MultipartFile file, LocalDate date, String username) throws IOException {

//         GCUser gcUser = gcUserRepository.findByUsername(username)
//                 .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));

//         String originalFileName = file.getOriginalFilename();

//         FilePath filePath = filePathRepository.findByPathDescription("Pdf Local Path").get();

//         String uploadDir = filePath.getFullPath() + "\\";

//         File directory = new File(uploadDir).getCanonicalFile();
//         if (!directory.exists()) {
//             directory.mkdirs();
//         }

//         boolean existingPdf = pdfRepository.existsByPdfTitle(title);

//         if (!existingPdf) {

//             String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9\\-_ ]", "").trim();
//             if (sanitizedTitle.isEmpty()) {
//                 throw new IllegalArgumentException("Invalid PDF title");
//             }

//             String fileName = (sanitizedTitle + ".pdf");

//             File destinationFile = new File(directory, fileName).getCanonicalFile();
//             if (!destinationFile.getPath().startsWith(directory.getPath() + File.separator)) {
//                 throw new SecurityException("Path traversal attempt detected.");
//             }

//             file.transferTo(destinationFile);

//             Pdf pdf = new Pdf();
//             pdf.setPdfTitle(title);
//             pdf.setFilePath(filePath);
//             pdf.setPdfFileName(originalFileName);
//             pdf.setDate(date);
//             pdf.setGcUser(gcUser);

//             pdfRepository.save(pdf);

//         } else {
//             throw new FileAlreadyExistsException(title + "file already exists.");

//         }
//     }

//     public List<Pdf> getImportantPdfs() {
//         return pdfRepository.findAll();
//     }

//     public ResponseEntity<Resource> getPdfResponse(Long id) throws IOException {

//         Optional<Pdf> optionalPdf = pdfRepository.findById(id);

//         if (optionalPdf.isPresent()) {

//             Pdf pdf = optionalPdf.get();

//             FilePath existingPdfPath = pdf.getFilePath();

//             // Resolve canonical base directory
//             File baseDir = new File(existingPdfPath.getFullPath()).getCanonicalFile();

//             String fileName = pdf.getPdfTitle() + ".pdf";

//             // Canonical path boundary check
//             File file = new File(baseDir, fileName).getCanonicalFile();
//             if (!file.getPath().startsWith(baseDir.getPath() + File.separator)) {
//                 throw new SecurityException("Path traversal attempt detected.");
//             }

//             if (!file.exists()) {
//                 return ResponseEntity.notFound().build();
//             }

//             Resource resource = new UrlResource(file.toURI());

//             return ResponseEntity.ok()
//                     .contentType(MediaType.APPLICATION_PDF)
//                     .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
//                     .body(resource);
//         }

//         return ResponseEntity.notFound().build();
//     }

//     public List<Pdf> display_Pdf_Admin() {
//         return pdfRepository.findAll();
//     }

//     public void deletePdf(Long id) throws IOException {

//         Optional<Pdf> optionalPdf = pdfRepository.findById(id);

//         if (optionalPdf.isPresent()) {

//             Pdf pdf = optionalPdf.get();

//             FilePath pdfPath = pdf.getFilePath();

//             String deletePath = pdfPath.getFullPath();

//             String fileName = pdf.getPdfTitle() + ".pdf";

//             // File File = new File(deletePath + fileName);
//             File baseDir = new File(deletePath).getCanonicalFile();
//             File fileToDelete = new File(baseDir, fileName).getCanonicalFile();
//             if (!fileToDelete.getPath().startsWith(baseDir.getPath() + File.separator)) {
//                 throw new SecurityException("Path traversal attempt detected.");
//             }

//             if (fileToDelete.exists()) {
//                 if (fileToDelete.delete()) {
//                     pdfRepository.deleteById(id);
//                 }
//             }
//         }
//     }
// }


package spring.aop.gazettemanagementnic.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.Pdf;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.PdfRepository;

@Service
@Slf4j
public class PdfService {

    @Autowired
    private GCUserRepository gcUserRepository;

    @Autowired
    private FilePathRepository filePathRepository;

    @Autowired
    private PdfRepository pdfRepository;

    // ✅ SECURE SAVE METHOD
    public void savePdf(String title, MultipartFile file, LocalDate date, String username) throws IOException {

        GCUser gcUser = gcUserRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // ✅ Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // ✅ File size limit (20MB)
        final long MAX_SIZE = 20 * 1024 * 1024;
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File size exceeds limit");
        }

        // ✅ Validate content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        // ✅ Validate title (only for DB, NOT for filename)
        String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9\\-_ ]", "").trim();
        if (sanitizedTitle.isEmpty()) {
            throw new IllegalArgumentException("Invalid PDF title");
        }

        // ✅ Generate SAFE filename (CRITICAL FIX)
        String fileName = UUID.randomUUID().toString() + ".pdf";

        log.info("Generated secure filename: {}", fileName);

        // ✅ Get upload directory
        FilePath filePath = filePathRepository.findByPathDescription("Pdf Local Path")
                .orElseThrow(() -> new IllegalArgumentException("Upload path not configured"));

        String uploadDir = filePath.getFullPath();

        Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // ✅ Create directory safely
        Files.createDirectories(basePath);

        // ✅ Resolve secure path
        Path resolvedPath = basePath.resolve(fileName).normalize();

        if (!resolvedPath.startsWith(basePath)) {
            throw new SecurityException("Invalid file path");
        }

        // ✅ Save file
        file.transferTo(resolvedPath.toFile());

        // ✅ Save to DB
        Pdf pdf = new Pdf();
        pdf.setPdfTitle(sanitizedTitle); // store clean title
        pdf.setPdfFileName(fileName);    // store actual filename
        pdf.setFilePath(filePath);
        pdf.setDate(date);
        pdf.setGcUser(gcUser);

        pdfRepository.save(pdf);
    }

    public List<Pdf> getImportantPdfs() {
        return pdfRepository.findAll();
    }

    // ✅ SECURE FETCH
    public ResponseEntity<Resource> getPdfResponse(Long id) throws IOException {

        Optional<Pdf> optionalPdf = pdfRepository.findById(id);

        if (optionalPdf.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Pdf pdf = optionalPdf.get();

        FilePath filePath = pdf.getFilePath();

        Path basePath = Paths.get(filePath.getFullPath()).toAbsolutePath().normalize();

        String fileName = pdf.getPdfFileName(); // ✅ use stored filename

        Path filePathResolved = basePath.resolve(fileName).normalize();

        // ✅ Prevent path traversal
        if (!filePathResolved.startsWith(basePath)) {
            throw new SecurityException("Path traversal attempt detected");
        }

        // ✅ Check file
        if (!Files.exists(filePathResolved) || !Files.isReadable(filePathResolved)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePathResolved.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + pdf.getPdfTitle() + ".pdf\"")
                .body(resource);
    }

    public List<Pdf> display_Pdf_Admin() {
        return pdfRepository.findAll();
    }

    // ✅ SECURE DELETE
    public void deletePdf(Long id) throws IOException {

        Optional<Pdf> optionalPdf = pdfRepository.findById(id);

        if (optionalPdf.isEmpty()) {
            return;
        }

        Pdf pdf = optionalPdf.get();

        FilePath filePath = pdf.getFilePath();

        Path basePath = Paths.get(filePath.getFullPath()).toAbsolutePath().normalize();

        String fileName = pdf.getPdfFileName(); // ✅ use stored filename

        Path fileToDelete = basePath.resolve(fileName).normalize();

        // ✅ Prevent traversal
        if (!fileToDelete.startsWith(basePath)) {
            throw new SecurityException("Path traversal attempt detected");
        }

        // ✅ Delete file
        if (Files.exists(fileToDelete)) {
            Files.delete(fileToDelete);
        }

        pdfRepository.deleteById(id);
    }
}