package spring.aop.gazettemanagementnic.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactUsDto {

    private Long contactId;

    @NotBlank(message = "Contact table is required")
    private String contactTable;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String designation;

    @Pattern(regexp = "\\d{3,5}", message = "STD code must be 3 to 5 digits")
    private String stdCode;

    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phno;

    @Pattern(regexp = "\\d{10}", message = "Mobile must be 10 digits")
    private String mobile;

    // Getters and Setters
}