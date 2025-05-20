package spring.aop.gazettemanagementnic.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
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
import spring.aop.gazettemanagementnic.entity.Pdf;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.PdfRepository;

@Service
public class PdfService {


    @Autowired
    GCUserRepository gcUserRepository;

    @Autowired
    FilePathRepository filePathRepository;

    @Autowired
    PdfRepository pdfRepository;

    public void savePdf(String title, MultipartFile file, LocalDate date, String username) throws IOException {
        

        GCUser gcUser = gcUserRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + username));

        String originalFileName = file.getOriginalFilename();

        FilePath filePath = filePathRepository.findByPathDescription("Pdf Local Path").get();

        String uploadDir = filePath.getFullPath()+ "\\";

        File directory = new File(uploadDir);
        if (!directory.exists()){
            directory.mkdirs();
        }

        boolean existingPdf = pdfRepository.existsByPdfTitle(title);

        if(!existingPdf){

            String fileName = (title + ".pdf");

            File destinationFile = new File(uploadDir + fileName);

            file.transferTo(destinationFile);


            Pdf pdf = new Pdf();
            pdf.setPdfTitle(title);
            pdf.setFilePath(filePath);
            pdf.setPdfFileName(originalFileName);
            pdf.setDate(date);
            pdf.setGcUser(gcUser);

            pdfRepository.save(pdf);
            
            

        }else{
             throw new FileAlreadyExistsException( title + "file already exists.");

        }
    }



    public List<Pdf> getImportantPdfs(){
        return pdfRepository.findAll();
    }
    

        public ResponseEntity<Resource> getPdfResponse(Long id) throws IOException {

            Optional<Pdf> optionalPdf = pdfRepository.findById(id);
            

            if(optionalPdf.isPresent()){
                
                Pdf pdf = optionalPdf.get();

                
                FilePath existingPdfPath = pdf.getFilePath();

                String filePath = existingPdfPath.getFullPath();

                filePath = filePath + pdf.getPdfTitle()+".pdf";

                File file = new File(filePath);

                if(!file.exists()){
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



        public List<Pdf> display_Pdf_Admin(){
            return pdfRepository.findAll();
        }

    



        public void deletePdf(Long id){

            Optional<Pdf> optionalPdf = pdfRepository.findById(id);

            if (optionalPdf.isPresent()) {
                
                Pdf pdf = optionalPdf.get();

                
                FilePath pdfPath = pdf.getFilePath();

                String deletePath = pdfPath.getFullPath();

                String fileName = pdf.getPdfTitle() + ".pdf";

                File File = new File(deletePath + fileName);

                if (File.exists()) {
                    if (File.delete()) {
                        pdfRepository.deleteById(id);
                    }
                }
            }
        }
}
