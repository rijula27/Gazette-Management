package spring.aop.gazettemanagementnic.entity;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
@Table(name = "gazette")
public class Gazette {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gId;

    @Column(nullable = false)
    private LocalDate date;



    @Column(nullable = false)
    private String part;


    @Column(nullable = false)
    private String fileName;


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


    

    

    public Long getId() {
        return gId;
    }


    public void setId(Long id) {
        this.gId = id;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public String getPart() {
        return part;
    }


    public void setPart(String part) {
        this.part = part;
    }


    public String getFileName() {
        return fileName;
    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
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

   
