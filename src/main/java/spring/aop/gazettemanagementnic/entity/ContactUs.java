package spring.aop.gazettemanagementnic.entity;

import java.time.LocalDate;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="contactUs")
public class ContactUs {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;


    @Column(nullable = false)
    @Size( max = 30, message = "Name maximum character limit 30")
    private String name;

    @Column(nullable = false)
    @Size( max = 30, message = "Designation maximum character limit 30")
    private String designation;

    @Column(nullable = false)
    @Size( max = 8, message = "STD code maximum character limit 8")
    private String stdCode;

    @Column(nullable = false)
    @Size( max = 15, message = "Phone number maximum character limit 15")
    private String phno;


    @Size( max = 12, message = "Mobile number maximum character limit 12")
    private String mobile;

    @Column(nullable = false)
    @Size( max = 30, message = "Contact Table name maximum character limit 30")
    private String contactTable;

    @Column(nullable = false)
    private LocalDate date;



    @ManyToOne
    @JoinColumn(name = "gcuser_id", nullable = false)
    private GCUser gcUser;

    
    

    public Long getContactId() {
        return contactId;
    }

   
    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getDesignation() {
        return designation;
    }

    
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    
    public String getPhno() {
        return phno;
    }

    
    public void setPhno(String phno) {
        this.phno = phno;
    }

    /**
     * @return String return the contactTable
     */
    public String getContactTable() {
        return contactTable;
    }

    /**
     * @param contactTable the contactTable to set
     */
    public void setContactTable(String contactTable) {
        this.contactTable = contactTable;
    }

    /**
     * @return LocalDate return the date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return GCUser return the gcUser
     */
    public GCUser getGcUser() {
        return gcUser;
    }

    /**
     * @param gcUser the gcUser to set
     */
    public void setGcUser(GCUser gcUser) {
        this.gcUser = gcUser;
    }


    


    /**
     * @return String return the stdCode
     */
    public String getStdCode() {
        return stdCode;
    }

    /**
     * @param stdCode the stdCode to set
     */
    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }

    /**
     * @return String return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile the mobile to set
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
