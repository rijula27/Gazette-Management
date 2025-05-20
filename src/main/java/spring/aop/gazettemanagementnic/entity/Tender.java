package spring.aop.gazettemanagementnic.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "tender")
public class Tender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tId;


    @Column(nullable = false, unique = true)
    @Size( max = 150, message = "Title maximum character limit 150")
    private String title;

    @Column(nullable = false, unique = true)
    @Size( max = 100, message = "Title length maximum character limit 100")
    // @Pattern( regexp = "^[A-Za-z0-9_\\-/\\s]{1,50}$", message = "ref_no can be up to 50 characters and may contain only letters, numbers, _ - / and spaces")
    private String refNo;


    @Column(nullable = false)
    private LocalDate announcement_Date;

    @Column(nullable = false)
    private LocalDate last_Date;

    @Column(nullable = false)
    private LocalDate opening_Date;

    @Column
    @Size( max = 150, message = "Keywords maximum character limit 150")
    private String keywords;



    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;


    @ManyToOne
    @JoinColumn(name = "pathId", nullable = false)
    private FilePath filePath;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private GCUser gcUser;



    @ManyToOne
    @JoinColumn(name = "edit_id", nullable = true)
    private GCUser gcUser_edit;


    
    
    public Long getTId() {
        return tId;
    }


    public void setTId(Long tId) {
        this.tId = tId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getRef_No() {
        return refNo;
    }


    public void setRef_No(String ref_No) {
        this.refNo = ref_No;
    }


    public LocalDate getAnnouncement_Date() {
        return announcement_Date;
    }

    public void setAnnouncement_Date(LocalDate announcement_Date) {
        this.announcement_Date = announcement_Date;
    }


    public LocalDate getLast_Date() {
        return last_Date;
    }


    public void setLast_Date(LocalDate last_Date) {
        this.last_Date = last_Date;
    }

    public LocalDate getOpening_Date() {
        return opening_Date;
    }


    public void setOpening_Date(LocalDate opening_Date) {
        this.opening_Date = opening_Date;
    }


    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }


    public Status getStatus() {
        return status;
    }


    public void setStatus(Status status) {
        this.status = status;
    }


    public FilePath getFilePath() {
        return filePath;
    }


    public void setFilePath(FilePath filePath) {
        this.filePath = filePath;
    }


    public GCUser getGcUser() {
        return gcUser;
    }


    public void setGcUser(GCUser gcUser) {
        this.gcUser = gcUser;
    }

    public GCUser getGcUser_edit() {
        return gcUser_edit;
    }


    public void setGcUser_edit(GCUser gcUser_edit) {
        this.gcUser_edit = gcUser_edit;
    }

}
