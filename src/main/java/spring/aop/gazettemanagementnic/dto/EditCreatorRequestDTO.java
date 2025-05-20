package spring.aop.gazettemanagementnic.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class EditCreatorRequestDTO {

    private String userName;
    private String newUserName;
    private String existingUserPassword;
    private String newUserPassword;
    private String userConfirmPassword;
    private String adminPassword;


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getNewUserName() {
        return newUserName;
    }


    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }


    public String getExistingUserPassword() {
        return existingUserPassword;
    }


    public void setExistingUserPassword(String existingUserPassword) {
        this.existingUserPassword = existingUserPassword;
    }


    public String getNewUserPassword() {
        return newUserPassword;
    }


    public void setNewUserPassword(String newUserPassword) {
        this.newUserPassword = newUserPassword;
    }


    public String getUserConfirmPassword() {
        return userConfirmPassword;
    }


    public void setUserConfirmPassword(String userConfirmPassword) {
        this.userConfirmPassword = userConfirmPassword;
    }


    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

}
