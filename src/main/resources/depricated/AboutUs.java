package spring.aop.gazettemanagementnic.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
public class AboutUs {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sectionId;

    @Column(nullable = false)
    @Size( max = 150, message = "Heading maximum character limit 150")
    @Pattern(regexp = "^[a-zA-Z0-9\\- ]+$", message = "Heading can only contain letters, numbers, hyphens, and spaces")
    private String sectionHeading;

    @Column(nullable = false)
    @Size( max = 2000, message = "Content maximum character limit 2000")
    @Pattern(regexp = "^[a-zA-Z0-9./\\-@#$&!():;,_? \\n\\r]+$", message = "Content contains unsupported characters")
    private String sectionContent;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "gcuser_id", nullable = false)
    private GCUser gcUser;



    

    /**
     * @return Long return the sectionId
     */
    public Long getSectionId() {
        return sectionId;
    }

    /**
     * @param sectionId the sectionId to set
     */
    public void setSectionId(Long sectionId) {
        this.sectionId = sectionId;
    }

    /**
     * @return String return the sectionHeading
     */
    public String getSectionHeading() {
        return sectionHeading;
    }

    /**
     * @param sectionHeading the sectionHeading to set
     */
    public void setSectionHeading(String sectionHeading) {
        this.sectionHeading = sectionHeading;
    }

    /**
     * @return String return the sectionContent
     */
    public String getSectionContent() {
        return sectionContent;
    }

    /**
     * @param sectionContent the sectionContent to set
     */
    public void setSectionContent(String sectionContent) {
        this.sectionContent = sectionContent;
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

}
