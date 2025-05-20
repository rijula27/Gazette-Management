package spring.aop.gazettemanagementnic.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.aop.gazettemanagementnic.entity.AboutUs;
import spring.aop.gazettemanagementnic.entity.ContactUs;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.repository.AboutUsRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;

@Service
public class AboutUsService {

    @Autowired
    private GCUserRepository gcUserRepository;

    @Autowired
    private AboutUsRepository aboutUsRepository;

    public String saveAbout(String sectionHeading, String sectionContent, String adminName, LocalDate now) {
    
        GCUser gcUser = gcUserRepository.findByUsername(adminName)
        .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));

        AboutUs aboutUs = new AboutUs();

        aboutUs.setSectionHeading(sectionHeading);
        aboutUs.setSectionContent(sectionContent);
        aboutUs.setGcUser(gcUser);
        aboutUs.setDate(now);


        aboutUsRepository.save(aboutUs);
        return "Content created succesfully";

    
    }

    public List<AboutUs> displayContent() {

        return aboutUsRepository.findAll();
    }

    public void deleteContent(Long id) {
       Optional<AboutUs> content = aboutUsRepository.findById(id);

        if(content.isPresent()){
            aboutUsRepository.deleteById(id);
        }    
    }

    public String editContent(Long sectionId, String sectionHeading, String sectionContent, String adminName,
            LocalDate now) {

                Optional<AboutUs> content = aboutUsRepository.findById(sectionId);

                if(content.isPresent()){

                    GCUser gcUser = gcUserRepository.findByUsername(adminName)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));
            
                    AboutUs aboutUs = content.get();

                    aboutUs.setSectionHeading(sectionHeading);
                    aboutUs.setSectionContent(sectionContent);
                    aboutUs.setDate(now);
                    aboutUs.setGcUser(gcUser);

                    aboutUsRepository.save(aboutUs);
                    return "Content edited succesfully ";
                }else{
                    throw new NoSuchElementException("Contact not found for ID: " + sectionId);

                }

            }
    
}
