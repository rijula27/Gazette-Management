package spring.aop.gazettemanagementnic.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import spring.aop.gazettemanagementnic.entity.ContactUs;
import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.repository.ContactUsRepository;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;

@Service
public class ContactUsService {

    @Autowired
    GCUserRepository gcUserRepository;

    @Autowired
    ContactUsRepository contactUsRepository;

    public String saveContact(String contactTable, String name, String designation, String std, String phone,
            String mobile, String adminName,
            LocalDate date) throws IOException {

        GCUser gcUser = gcUserRepository.findByUsername(adminName)
                .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));

        ContactUs contactUs = new ContactUs();

        contactUs.setContactTable(contactTable);
        contactUs.setName(name);
        contactUs.setDesignation(designation);
        contactUs.setStdCode(std);
        contactUs.setPhno(phone);
        contactUs.setMobile(mobile);
        contactUs.setDate(date);
        contactUs.setGcUser(gcUser);

        contactUsRepository.save(contactUs);
        return "Contact created successfully";

    }

    public List<ContactUs> displayContact() {
        return contactUsRepository.findAll();
    }

    public String editContact(Long contactId, String name, String designation, String std, String phno, String mobile,
            String adminName,
            LocalDate now) {

        Optional<ContactUs> contact = contactUsRepository.findById(contactId);

        if (contact.isPresent()) {

            GCUser gcUser = gcUserRepository.findByUsername(adminName)
                    .orElseThrow(() -> new IllegalArgumentException("User not found for username: " + adminName));

            ContactUs contactUs = contact.get();

            contactUs.setName(name);
            contactUs.setDesignation(designation);
            contactUs.setStdCode(std);
            contactUs.setPhno(phno);
            contactUs.setMobile(mobile);
            contactUs.setDate(now);
            contactUs.setGcUser(gcUser);

            contactUsRepository.save(contactUs);
            return "Contact edited succesfully " + name;
        } else {
            throw new NoSuchElementException("Contact not found for ID: " + contactId);

        }

    }

    public void deleteContact(Long id) {

        Optional<ContactUs> contact = contactUsRepository.findById(id);

        if (contact.isPresent()) {
            contactUsRepository.deleteById(id);
        }

    }

    public List<ContactUs> getAllContact() {

        return contactUsRepository.findAll();
    }

}
