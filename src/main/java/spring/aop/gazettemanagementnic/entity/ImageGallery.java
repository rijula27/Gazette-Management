package spring.aop.gazettemanagementnic.entity;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ImageGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false)
    private String imageTitle;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate uploadDate;

    @ManyToOne
    @JoinColumn(name = "pathId", nullable = false)
    private FilePath filePath;

    @ManyToOne
    @JoinColumn(name = "gcuser_id", nullable = false)
    private GCUser gcUser;

    /**
     * @return Long return the imageId
     */
    public Long getImageId() {
        return imageId;
    }

    /**
     * @param imageId the imageId to set
     */
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    /**
     * @return String return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return LocalDate return the uploadDate
     */
    public LocalDate getUploadDate() {
        return uploadDate;
    }

    /**
     * @param uploadDate the uploadDate to set
     */
    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
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
     * @return String return the imageTitle
     */
    public String getImageTitle() {
        return imageTitle;
    }

    /**
     * @param imageTitle the imageTitle to set
     */
    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    /**
     * @return FilePath return the filePath
     */
    public FilePath getFilePath() {
        return filePath;
    }

    /**
     * @param filePath the filePath to set
     */
    public void setFilePath(FilePath filePath) {
        this.filePath = filePath;
    }

}
