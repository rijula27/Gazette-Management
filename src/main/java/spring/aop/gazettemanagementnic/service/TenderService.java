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

import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.Status;
import spring.aop.gazettemanagementnic.entity.Tender;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.StatusRepository;
import spring.aop.gazettemanagementnic.repository.TenderRepository;

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
                              MultipartFile  file, String keywords) throws IOException {



        GCUser gcUser = gcUserRepository.findByUsername(username).get();               






        int year = announcementDate.getYear();


        FilePath filePath = filePathRepository.findByPathDescription("Tender Local Path").get();

        String uploadDir = filePath.getFullPath() + year + "\\";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        String fileName = title + ".pdf";

        File destinationFile = new File(uploadDir + fileName);

        file.transferTo(destinationFile);

        Status createStatus = statusRepository.findByState("Create").get();


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


    public List<Tender> displayTender(String username){

        return tenderRepository.findAllByGcUser_UsernameAndStatus_State(username, "Create");
    }





    public ResponseEntity<Resource> getTenderPdfResponse(Long id) throws IOException {
        Optional<Tender> optionalTender = tenderRepository.findById(id);  // same as before

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();

            FilePath existinTenderPath = tender.getFilePath();
    

            String filePath = existinTenderPath.getFullPath();

            int year = tender.getAnnouncement_Date().getYear();
            String title = tender.getTitle();     // e.g., I

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


        Optional<Tender> optionalTender = tenderRepository.findById(id);

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();

            FilePath tenderPath = tender.getFilePath();


            int year = tender.getAnnouncement_Date().getYear();

            String deletePath = tenderPath.getFullPath();


            String deletedDir = deletePath + year + "\\";



            String fileName = tender.getTitle() + ".pdf";

            File File = new File(deletedDir + fileName);


            if (File.exists()) {
                if (File.delete()) {
                    tenderRepository.deleteById(id);
                }
            }


        }


    }




    public void send_tender_Publisher(Long id){
        Optional<Tender> optionalTender = tenderRepository.findById(id);

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();
        
            Status sendBackStatus = statusRepository.findByState("Send").get();


            tender.setStatus(sendBackStatus);

            tenderRepository.save(tender);

        }
    }




    public String updateTender(Long id,String title, String username,  LocalDate submissionLastDate,LocalDate openingDate, MultipartFile file) throws IOException {

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
        String uploadDir = tenderPath.getFullPath() + year + "\\";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String newFileName = title + ".pdf";
        File newFile = new File(uploadDir + newFileName);
        if (file != null && !file.isEmpty()) {
            file.transferTo(newFile);
        }

        tender.setLast_Date(submissionLastDate);
        tender.setOpening_Date(openingDate);
        tender.setGcUser_edit(gcUser_edit);
        tender.setFilePath(tenderPath); // Update file path if needed

        tenderRepository.save(tender);

        return "Tender updated successfully!";
    }


    public List<Tender> display_Tender_Publisher() {
        return tenderRepository.findAllByStatus_State( "Send");
    }



    public void send_Back_tender_Creator(Long id){
        Optional<Tender> optionalTender = tenderRepository.findById(id);

        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();
           
            Status sendBackStatus = statusRepository.findByState("Create").get();


            tender.setStatus(sendBackStatus);

            tenderRepository.save(tender);

        }
    }


    public void published_tender(Long id){
        Optional<Tender> optionalTender = tenderRepository.findById(id);
        if (optionalTender.isPresent()) {

            Tender tender = optionalTender.get();
           
            Status publishedStatus = statusRepository.findByState("Published").get();


            tender.setStatus(publishedStatus);

            tenderRepository.save(tender);

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
        


        public List<Tender> getActiveTenders(){
            LocalDate today = LocalDate.now();
            LocalDate oneMonthAgo = today.minusMonths(1);
            return tenderRepository.findActiveTenders(today, oneMonthAgo);
            
        }




        public List<Integer> getAvailableYears(){
            return tenderRepository.findDistinctYears();
        }



        public List<Integer> getAvailableMonths(Integer year) {
            return tenderRepository.findDistinctMonthsByYear(year);
        }


        public List<Tender> getTendersByDate(Integer year, Integer month) {

            return tenderRepository.findByYearAndMonthAndStatus_State(year, month, "Published");
        }
}

    

