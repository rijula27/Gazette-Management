package spring.aop.gazettemanagementnic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="filePath")
public class FilePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pathId;


    @Column(nullable = false)
    private String fullPath;


    @Column(nullable = false)
    private String pathDescription;




   
    public Long getPathId() {
        return pathId;
    }

   
    public void setPathId(Long pathId) {
        this.pathId = pathId;
    }

   
    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    
    public String getPathDescription() {
        return pathDescription;
    }

    
    public void setPathDescription(String pathDescription) {
        this.pathDescription = pathDescription;
    }

}
