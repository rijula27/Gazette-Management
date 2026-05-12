package spring.aop.gazettemanagementnic.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import spring.aop.gazettemanagementnic.entity.Status;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.StatusRepository;
import spring.aop.gazettemanagementnic.repository.TenderRepository;

@Slf4j
@Service
public class TenderService {

    @Autowired
    private TenderRepository tenderRepository;

    @Autowired
    private GCUserRepository gcUserRepository;

    @Autowired
    private FilePathRepository filePathRepository;

    @Autowired
    private StatusRepository statusRepository;

    public String save_tender(String username, String title, String referenceNumber,
            LocalDate announcementDate, LocalDate submissionLastDate, LocalDate openingDate,
            MultipartFile file, String keywords) throws IOException {

        // ✅ Validate file
        validatePdfFile(file);

        // ✅ Validate dates
        if (announcementDate == null || submissionLastDate == null || openingDate == null) {
            throw new IllegalArgumentException("Dates cannot be null");
        }

        GCUser gcUser = gcUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        int year = announcementDate.getYear();

        FilePath filePath = filePathRepository.findByPathDescription("Tender Local Path")
                .orElseThrow(() -> new RuntimeException("Tender path not found"));

        // ✅ Safe directory using File.separator
        File baseDir = new File(filePath.getFullPath(), String.valueOf(year)).getCanonicalFile();
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // ✅ Sanitize title for filename
        String sanitizedTitle = title.trim()
                .replaceAll("[^a-zA-Z0-9-_ ]", "");
        if (sanitizedTitle.isBlank()) {
            throw new IllegalArgumentException("Invalid tender title");
        }
        if (sanitizedTitle.length() > 100) {
            throw new IllegalArgumentException("Title too long");
        }

        String fileName = sanitizedTitle + ".pdf";

        // ✅ Path traversal check
        File destinationFile = new File(baseDir, fileName).getCanonicalFile();
        if (!destinationFile.getPath().startsWith(baseDir.getPath() + File.separator)) {
            throw new SecurityException("Path traversal attempt detected");
        }

        file.transferTo(destinationFile);

        Status createStatus = statusRepository.findByState("Create")
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Tender tender = new Tender();
        tender.setTitle(title);
        tender.setRef_No(referenceNumber);
        tender.setAnnouncement_Date(announcementDate);
        tender.setKeywords(keywords);
        tender.setLast_Date(submissionLastDate);
        tender.setOpening_Date(openingDate);
        tender.setGcUser(gcUser);
        tender.setStatus(createStatus);
        tender.setFilePath(filePath);
        tender.setGcUser_edit(null);

        tenderRepository.save(tender);
        return "Tender saved successfully!";
    }

    public List<Tender> displayTender(String username) {

        return tenderRepository.findAllByGcUser_UsernameAndStatus_State(username, "Create");
    }

    public ResponseEntity<Resource> getTenderPdfResponse(Long id) throws IOException {
        Optional<Tender> optionalTender = tenderRepository.findById(id); // same as before

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();

            FilePath existinTenderPath = tender.getFilePath();

            String filePath = existinTenderPath.getFullPath();

            int year = tender.getAnnouncement_Date().getYear();
            String title = tender.getTitle(); // e.g., I

            filePath = filePath + year + "\\" + title + ".pdf";

            File file = new File(filePath);

            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(file.toURI());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    public void deleteTender(Long id) {
        log.info("Attempting to delete tender with ID: " + id);

        Optional<Tender> optionalTender = tenderRepository.findById(id);

        if (optionalTender.isPresent()) {

            log.info("Tender found with ID: " + id);
            Tender tender = optionalTender.get();

            FilePath tenderPath = tender.getFilePath();

            int year = tender.getAnnouncement_Date().getYear();

            String deletePath = tenderPath.getFullPath();

            String deletedDir = deletePath + year + "\\";

            String fileName = tender.getTitle() + ".pdf";

            File File = new File(deletedDir + fileName);

            if (File.exists()) {
                log.info(" file exist : " + fileName);
                if (File.delete()) {
                    tenderRepository.deleteById(id);
                }
            }

        }

    }

    public void send_tender_Publisher(Long id) {
        Optional<Tender> optionalTender = tenderRepository.findById(id);

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();

            Status sendBackStatus = statusRepository.findByState("Send").get();

            tender.setStatus(sendBackStatus);

            tenderRepository.save(tender);

        }
    }

    // public String updateTender(Long id, String title, String username, LocalDate
    // submissionLastDate,
    // LocalDate openingDate, MultipartFile file) throws IOException {

    // Optional<Tender> optionalTender = tenderRepository.findById(id);
    // if (optionalTender.isEmpty()) {
    // throw new RuntimeException("Tender not found with ID: " + id);
    // }

    // Tender tender = optionalTender.get();

    // GCUser gcUser_edit = gcUserRepository.findByUsername(username)
    // .orElseThrow(() -> new RuntimeException("User not found: " + username));

    // FilePath tenderPath = filePathRepository.findByPathDescription("Tender Local
    // Path")
    // .orElseThrow(() -> new RuntimeException("Tender path not found"));

    // int year = tender.getAnnouncement_Date().getYear();
    // String uploadDir = tenderPath.getFullPath() + year + File.separator;

    // File directory = new File(uploadDir).getCanonicalFile();
    // if (!directory.exists()) {
    // directory.mkdirs();
    // }

    // String sanitizedTitle = title.trim()
    // .replace("/", "")
    // .replace("\\", "")
    // .replace(":", "")
    // .replace("\0", "");

    // if (sanitizedTitle.isEmpty()) {
    // throw new IllegalArgumentException("Invalid tender title.");
    // }

    // String newFileName = sanitizedTitle + ".pdf";
    // File newFile = new File(directory, newFileName).getCanonicalFile();
    // if (!newFile.getPath().startsWith(directory.getPath() + File.separator)) {
    // throw new SecurityException("Path traversal attempt detected.");
    // }

    // if (file != null && !file.isEmpty()) {
    // file.transferTo(newFile);
    // }

    // tender.setLast_Date(submissionLastDate);
    // tender.setOpening_Date(openingDate);
    // tender.setGcUser_edit(gcUser_edit);
    // tender.setFilePath(tenderPath); // Update file path if needed

    // tenderRepository.save(tender);

    // return "Tender updated successfully!";
    // }

    public String updateTender(Long id, String title, String username,
            LocalDate submissionLastDate,
            LocalDate openingDate,
            MultipartFile file) throws IOException {

        if (file != null && !file.isEmpty()) {
            validatePdfFile(file);
        }

        Optional<Tender> optionalTender = tenderRepository.findById(id);
        if (optionalTender.isEmpty()) {
            throw new RuntimeException("Tender not found with ID: " + id);
        }

        Tender tender = optionalTender.get();

        GCUser gcUser_edit = gcUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        FilePath tenderPath = filePathRepository.findByPathDescription("Tender Local Path")
                .orElseThrow(() -> new RuntimeException("Tender path not found"));

        int year = tender.getAnnouncement_Date().getYear();

        // ✅ BASE DIRECTORY (STRICT CONTROLLED PATH)
        File baseDir = new File(tenderPath.getFullPath(), String.valueOf(year)).getCanonicalFile();

        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IOException("Failed to create directory");
        }

        // ✅ STRICT WHITELIST VALIDATION (AUDIT FRIENDLY)
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }

        String sanitizedTitle = title.trim()
                .replaceAll("[^a-zA-Z0-9-_ ]", ""); // allowlist approach

        if (sanitizedTitle.isBlank()) {
            throw new IllegalArgumentException("Invalid tender title");
        }

        // OPTIONAL HARD LIMIT (prevents abuse / scanner flags)
        if (sanitizedTitle.length() > 100) {
            throw new IllegalArgumentException("Title too long");
        }

        String newFileName = sanitizedTitle + ".pdf";

        // ✅ SAFE FILE CREATION
        File newFile = new File(baseDir, newFileName).getCanonicalFile();

        // ✅ PATH ESCAPE PROTECTION (CRITICAL AUDIT CHECK)
        if (!newFile.getPath().startsWith(baseDir.getPath() + File.separator)) {
            throw new SecurityException("Path traversal detected");
        }

        // ✅ FILE WRITE ONLY IF PRESENT
        if (file != null && !file.isEmpty()) {
            file.transferTo(newFile);
        }

        tender.setLast_Date(submissionLastDate);
        tender.setOpening_Date(openingDate);
        tender.setGcUser_edit(gcUser_edit);

        // keep reference consistent
        tender.setFilePath(tenderPath);

        tenderRepository.save(tender);

        return "Tender updated successfully!";
    }

    public List<Tender> display_Tender_Publisher() {
        return tenderRepository.findAllByStatus_State("Send");
    }

    public void send_Back_tender_Creator(Long id) {
        Optional<Tender> optionalTender = tenderRepository.findById(id);

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();

            Status sendBackStatus = statusRepository.findByState("Create").get();

            tender.setStatus(sendBackStatus);

            tenderRepository.save(tender);

        }
    }

    public void published_tender(Long id) {
        Optional<Tender> optionalTender = tenderRepository.findById(id);
        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();

            Status publishedStatus = statusRepository.findByState("Published").get();

            tender.setStatus(publishedStatus);

            tenderRepository.save(tender);

        }
    }

    private void validatePdfFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Invalid file type. Only PDF allowed");
        }

        // Magic bytes check — PDF must start with %PDF
        byte[] bytes = new byte[4];
        file.getInputStream().read(bytes);
        if (bytes[0] != 0x25 || bytes[1] != 0x50 ||
                bytes[2] != 0x44 || bytes[3] != 0x46) {
            throw new IllegalArgumentException("File content is not a valid PDF");
        }
    }

    public List<Tender> display_Tender_Admin() {
        return tenderRepository.findAllByStatus_State("Published");
    }

    public List<Tender> displayAllTender(String username) {
        return tenderRepository.findAllByGcUser_Username(username);

    }

    public List<Tender> display_approved_Tender(String username) {
        return tenderRepository.findAllByGcUser_UsernameAndStatus_State(username, "Published");

    }

    public List<Tender> display_send_Tender(String username) {
        return tenderRepository.findAllByGcUser_UsernameAndStatus_State(username, "Send");

    }

    public List<Tender> display_create_Tender(String username) {
        return tenderRepository.findAllByGcUser_UsernameAndStatus_State(username, "Create");

    }

    public List<Tender> displayPublisherAllTender() {

        return tenderRepository.findAllByStatus_State("Send");

    }

    public List<Tender> display_published_Tender() {

        return tenderRepository.findAllByStatus_State("Published");

    }

    public List<Tender> display_send_back_Tender() {

        return tenderRepository.findAllByStatus_State("Create");

    }

    public boolean isTenderExist(String title, String ref_No) {
        return tenderRepository.existsByTitleOrRefNo(title, ref_No);
    }

    public List<Tender> getActiveTenders() {
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);
        Status publishedStatus = statusRepository.findByState("Published")
                .orElseThrow(() -> new RuntimeException("Published status not found"));

        Long publisherStatusId = publishedStatus.getStatus_id();

        return tenderRepository.findPublishedTenders(publisherStatusId, today, oneMonthAgo);

    }

    public List<Integer> getAvailableYears() {
        return tenderRepository.findDistinctYears();
    }

    public List<Integer> getAvailableMonths(Integer year) {
        return tenderRepository.findDistinctMonthsByYear(year);
    }

    public List<Tender> getTendersByDate(Integer year, Integer month) {

        return tenderRepository.findByYearAndMonthAndStatus_State(year, month, "Published");
    }

    public Optional<Tender> findTenderById(Long id) {
        return tenderRepository.findById(id);
    }
}
