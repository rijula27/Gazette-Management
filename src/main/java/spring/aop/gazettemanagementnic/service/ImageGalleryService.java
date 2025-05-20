package spring.aop.gazettemanagementnic.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.entity.Gazette;
import spring.aop.gazettemanagementnic.entity.ImageGallery;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;
import spring.aop.gazettemanagementnic.repository.ImageGalleryRepository;

@Service
public class ImageGalleryService {

    @Autowired
    private GCUserRepository gcUserRepository;

    @Autowired
    private FilePathRepository filePathRepository;


    @Autowired
    private ImageGalleryRepository imageGalleryRepository;

    public void saveImage(MultipartFile image, String description, String adminName) throws IOException {
 
        GCUser gcUser = gcUserRepository.findByUsername(adminName)
            .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));


        String originalFilename = image.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;


        FilePath filePath = filePathRepository.findByPathDescription("Gallery Local Path").get();


        String uploadDir = filePath.getFullPath();

        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        ImageGallery existingImage = imageGalleryRepository.findByImageTitle(uniqueFilename);

        if(existingImage == null){

            File destinationFile = new File(uploadDir + uniqueFilename);


            image.transferTo(destinationFile);


            ImageGallery imageGallery = new ImageGallery();

            imageGallery.setImageTitle(uniqueFilename);
            imageGallery.setDescription(description);
            imageGallery.setFilePath(filePath);
            imageGallery.setGcUser(gcUser);
            imageGallery.setUploadDate(LocalDate.now());

            imageGalleryRepository.save(imageGallery);

        }else{
                throw new FileAlreadyExistsException("Image with same title already exist");
        }

    }

    public List<ImageGallery> displayImage() {
    
        return imageGalleryRepository.findAll();
    }


    
}
