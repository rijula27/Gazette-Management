package spring.aop.gazettemanagementnic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import spring.aop.gazettemanagementnic.entity.GCUser;
import spring.aop.gazettemanagementnic.repository.GCUserRepository;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GCUserService implements UserDetailsService {


    @Autowired
    private GCUserRepository gcUserRepository;



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      GCUser appUser = gcUserRepository.findByUsername(username)
        .orElseThrow(()->
             new UsernameNotFoundException("User not found."));
        

        return new User(
                appUser.getUsername(),
                appUser.getPassword(), // Use the stored (bcrypt-encoded) password directly
                Collections.singleton(new SimpleGrantedAuthority(appUser.getRole()))
        );

    }

    // Helper method to retrieve a user by username
    public Optional<GCUser> findByUsername(String username) {
        return gcUserRepository.findByUsername(username);
    }

    // Check if the raw password matches the encoded password
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }


    public List<GCUser> displayUser_List() {
        return gcUserRepository.findByRole("CREATOR");
    }



    public String saveUser(String userName, String userPassword, String adminName, LocalDate date )throws IOException{


        String role = "CREATOR";
        Optional<GCUser> existingUser = gcUserRepository.findByUsername(userName);


        if(!existingUser.isPresent()){


            GCUser gcUser = new GCUser();
            
            if (!passwordEncoder.matches(userPassword, gcUser.getPassword())) {
                
            
            gcUser.setUsername(userName);
            gcUser.setPassword(passwordEncoder.encode(userPassword));
            gcUser.setRole(role);
            gcUser.setDate(date);

            gcUserRepository.save(gcUser);
            return "User created successfully by admin: " + adminName;
            }else{
                return "Password already exist: " + adminName;
            }
        }else{
                throw new FileAlreadyExistsException("user with the same user name already exist");
            }


    }



    public void deleteUser(Long id){
       Optional<GCUser> gcUser = gcUserRepository.findById(id);

       if (gcUser.isPresent()) {
            gcUserRepository.deleteById(id);
       }

    }




    public ResponseEntity<String> editCreator(String userName, String newUserName, String existingUserPassword, 
                                           String newUserPassword, String userConfirmPassword, LocalDate date) {

    Optional<GCUser> checkGcUser = gcUserRepository.findByUsername(userName);

    if (checkGcUser.isPresent()) {

        GCUser gcUser = checkGcUser.get();

        // Check current password
        if (!passwordEncoder.matches(existingUserPassword, gcUser.getPassword())) {
            return ResponseEntity.badRequest().body("Existing password is incorrect.");
        }

        // Check if new password and confirm password match
        if (!newUserPassword.equals(userConfirmPassword)) {
            return ResponseEntity.badRequest().body("New password and confirmation do not match.");
        }

        // Update values
        gcUser.setUsername(newUserName);
        gcUser.setPassword(passwordEncoder.encode(userConfirmPassword));
        gcUser.setDate(date);

        gcUserRepository.save(gcUser);

        return ResponseEntity.ok("Saved successfully");
    } else {
        return ResponseEntity.badRequest().body("User not found");
    }
}


}

   
    


