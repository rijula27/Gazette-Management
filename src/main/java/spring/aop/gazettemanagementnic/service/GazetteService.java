package spring.aop.gazettemanagementnic.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.Status;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GazetteRepository;
import spring.aop.gazettemanagementnic.repository.StatusRepository;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
public class GazetteService {

    @Autowired
    private GazetteRepository gazetteRepository;

    @Autowired
    private StatusRepository gazetteStatusRepository;


    @Autowired
    private FilePathRepository filePathRepository;

    @Autowired
    private GCUserRepository gcUserRepository;

    public void saveGazette(String part, MultipartFile file, LocalDate date, String username) throws IOException {




        GCUser gcUser = gcUserRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));



        String originalFilename = file.getOriginalFilename();

        int year = date.getYear();
        int month = date.getMonthValue();

        FilePath filePath = filePathRepository.findByPathDescription("Local Path").get();

       

        String uploadDir = filePath.getFullPath() + year + "\\" + month + "\\";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }


        Gazette existingPart = gazetteRepository.findByDateAndPart(date, part);



            if (existingPart == null) {


                String fileName = (date + "-" + part + ".pdf");

                File destinationFile = new File(uploadDir + fileName);


                file.transferTo(destinationFile);


                Status createStatus = gazetteStatusRepository.findByState("Create").get();




                // Create a new Gazette entity and set its properties
                Gazette gazette = new Gazette();
                gazette.setPart(part);
                gazette.setDate(date);
                gazette.setFileName(originalFilename);
                gazette.setGcUser(gcUser);
                gazette.setStatus(createStatus); 
                gazette.setFilePath(filePath);
                gazette.setGcUser_edit(null);

                // Save the entity to the database
                gazetteRepository.save(gazette);
            }else{
                throw new FileAlreadyExistsException("file with the same part : " + part + " on the same date : " + date + " already exists.");
            }


    }


    // New method to display all gazettes without any filtering
    public List<Gazette> displayGazette(String username) {
        return gazetteRepository.findAllByGcUser_UsernameAndStatus_State(username, "Create");
    }


    public List<Gazette> displayAllGazette(String username) {
        return gazetteRepository.findAllByGcUser_Username(username);
    }



    public List<Gazette> displayPublisherAllGazette() {
        return gazetteRepository.findAllByStatus_State("Send");
    }


    public List<Gazette> display_approved_Gazette(String username) {
        return gazetteRepository.findAllByGcUser_UsernameAndStatus_State(username, "Published");
    }


    public List<Gazette> display_published_Gazette() {
        return gazetteRepository.findAllByStatus_State("Published");
    }


    public List<Gazette> display_send_Gazette(String username) {
        return gazetteRepository.findAllByGcUser_UsernameAndStatus_State(username, "Send");
    }


    public List<Gazette> display_send_back_Gazette() {
        return gazetteRepository.findAllByStatus_State("Create");
    }


    public List<Gazette> display_create_Gazette(String username) {
        return gazetteRepository.findAllByGcUser_UsernameAndStatus_State(username, "Create");
    }

    public List<Gazette> displayGazette_Publisher() {
        return gazetteRepository.findAllByStatus_State("Send");
    }


    public void deleteGazette(Long id) {

        Optional<Gazette> optionalGazette = gazetteRepository.findById(id);

        if (optionalGazette.isPresent()) {
            Gazette gazette = optionalGazette.get();
            FilePath gazettePath = gazette.getFilePath();


            int year = gazette.getDate().getYear();
            int month = gazette.getDate().getMonthValue();


            String deletePath = gazettePath.getFullPath();


            String deletedDir = deletePath + year + "\\" + month + "\\";


            String fileName = (gazette.getDate() + "-" + gazette.getPart() + ".pdf");

            File File = new File(deletedDir + fileName);

            if (File.exists()) {
                if (File.delete()) {
                    gazetteRepository.deleteById(id);
                }
            }


        }


    }


    public void updateGazette(Long id, String part, MultipartFile file, String username, LocalDate date) throws IOException {

        int year = date.getYear();
        int month = date.getMonthValue();

        FilePath gazettePath = filePathRepository.findByPathDescription("Local Path").get();


        GCUser gcUser_edit = gcUserRepository.findByUsername(username).get();

        String uploadDir = gazettePath.getFullPath() + year + "\\" + month + "\\";


        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();

        Optional<Gazette> optionalGazette = gazetteRepository.findById(id);

        if (optionalGazette.isPresent()) {

            Gazette existingFiles = gazetteRepository.findByFileNameAndDate(file.getOriginalFilename(), date);
            Gazette existingPart = gazetteRepository.findByDateAndPart(date, part);

            if (existingFiles == null && existingPart == null) {
                Gazette gazette = optionalGazette.get();

                FilePath existinGazettePath = gazette.getFilePath();


                int existingFileYear = gazette.getDate().getYear();
                int existingFileMonth = gazette.getDate().getMonthValue();


                String existingFileUploadpath = existinGazettePath.getFullPath();

                String existingFileUploaddir = existingFileUploadpath + existingFileYear + "\\" + existingFileMonth + "\\";



                String existingfileName = (gazette.getDate() + "-" + gazette.getPart() + ".pdf");

                String fileName = (date + "-" + part + ".pdf");

                File existingFile = new File(existingFileUploaddir + existingfileName);

                File destinationFile = new File(uploadDir + fileName);


                if (existingFile.exists()) {
                    if (!existingFile.delete()) {
                        throw new IOException("Failed to delete old file: " + existingFile.getName());
                    }
                }

                if (file != null && !file.isEmpty()) {
                    file.transferTo(destinationFile);
                    gazette.setFileName(originalFilename);
                }

                gazette.setPart(part);
                gazette.setDate(date);

                gazette.setGcUser_edit(gcUser_edit);

                gazetteRepository.save(gazette);

            } else {
                throw new RuntimeException("Same name gazzette or same part gazzette already inserted for the date ");
            }


        }else {
            throw new RuntimeException("Gazette not found with ID: ");
        }
    }



    public void send_to_Publisher(Long id){
        Optional<Gazette> optionalGazette = gazetteRepository.findById(id);

        if (optionalGazette.isPresent()) {

            Gazette gazette = optionalGazette.get();
           
            Status sendBackStatus = gazetteStatusRepository.findByState("Send").get();


            gazette.setStatus(sendBackStatus);

            gazetteRepository.save(gazette);

        }
    }

    public void send_Back_Creator(Long id){
        Optional<Gazette> optionalGazette = gazetteRepository.findById(id);

        if (optionalGazette.isPresent()) {

            Gazette gazette = optionalGazette.get();
           
            Status sendBackStatus = gazetteStatusRepository.findByState("Create").get();


            gazette.setStatus(sendBackStatus);

            gazetteRepository.save(gazette);

        }
    }


    public void published(Long id){
        Optional<Gazette> optionalGazette = gazetteRepository.findById(id);
        if (optionalGazette.isPresent()) {

            Gazette gazette = optionalGazette.get();
           
            Status publishedStatus = gazetteStatusRepository.findByState("Published").get();


            gazette.setStatus(publishedStatus);

            gazetteRepository.save(gazette);

        }
    }

    public ResponseEntity<Resource> getGazettePdfResponse(Long id) throws IOException {
        Optional<Gazette> optionalGazette = gazetteRepository.findById(id);  // same as before

        if (optionalGazette.isPresent()) {

            Gazette gazette = optionalGazette.get();

            FilePath existinGazettePath = gazette.getFilePath();
    

            String filePath = existinGazettePath.getFullPath();

            int year = gazette.getDate().getYear();
            int month = gazette.getDate().getMonthValue();

            LocalDate fileDate = gazette.getDate();  // e.g., 2024-01-01
            String filePart = gazette.getPart();     // e.g., I

            // Final file path: "uploads/gazettes/2024-01-01-I.pdf"
            filePath = filePath + year + "\\" + month + "\\" + fileDate + "-" + filePart + ".pdf";

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

    




    public List<Gazette> displayGazette_Admin() {

        return gazetteRepository.findAllByStatus_State("Published");

    }



    public List<Integer> getAvailableYears(){
        return gazetteRepository.findDistinctYears();
    }



    public List<Integer> getAvailableMonths(Integer year) {
        return gazetteRepository.findDistinctMonthsByYear(year);
    }

    public List<Integer> getAvailableDates(Integer year, Integer month){
        return gazetteRepository.findDistinctDaysByYearAndMonth(year, month);
    }


    public List<Gazette> getGazettesByDate(Integer year, Integer month, Integer date) {

        LocalDate completeDate = LocalDate.of(year, month, date);

        return gazetteRepository.findByDateAndStatus_State(completeDate, "Published");

    }
}
