package spring.aop.gazettemanagementnic.entity;



import jakarta.persistence.*;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "status")
public class Status {
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long status_id;

    @Column(nullable = false)
    @Size(min = 2, max = 3, message = "Status code must be between 2 and 3 characters")
    private Long statusCode;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String description;



    

    

    public Long getStatus_id() {
        return status_id;
    }

    
    public void setStatus_id(Long status_id) {
        this.status_id = status_id;
    }

    
    public Long getStatusCode() {
        return statusCode;
    }

   
    public void setStatusCode(Long statusCode) {
        this.statusCode = statusCode;
    }

    
    public String getState() {
        return state;
    }

    
    public void setState(String state) {
        this.state = state;
    }

    
    public String getDescription() {
        return description;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

}
