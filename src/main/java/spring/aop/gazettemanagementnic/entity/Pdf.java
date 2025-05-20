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
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "pdf")
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pdfId;


    @Column(nullable =  false)
    @Size( max = 150, message = "Pdf title maximum character limit 150")
    private String pdfTitle;


    @Column(nullable =  false)
    @Size( max = 150, message = "Pdf title maximum character limit 150")
    private String pdfFileName;


    @ManyToOne
    @JoinColumn(name = "pathId", nullable = false)
    private FilePath filePath;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private GCUser gcUser;
    



    
    public Long getPdfId() {
        return pdfId;
    }


    public void setPdfId(Long pdfId) {
        this.pdfId = pdfId;
    }


    public String getPdfTitle() {
        return pdfTitle;
    }


    public void setPdfTitle(String pdfTitle) {
        this.pdfTitle = pdfTitle;
    }


    public FilePath getFilePath() {
        return filePath;
    }


    public void setFilePath(FilePath filePath) {
        this.filePath = filePath;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public GCUser getGcUser() {
        return gcUser;
    }

    
    public void setGcUser(GCUser gcUser) {
        this.gcUser = gcUser;
    }


    

    public String getPdfFileName() {
        return pdfFileName;
    }

    public void setPdfFileName(String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }

}
