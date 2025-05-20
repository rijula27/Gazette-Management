package spring.aop.gazettemanagementnic.dto;

import java.time.LocalDate;

public class GazetteUploadRequest  {
    
    private String gazettePart;
    private LocalDate date;
    private String username;
    private String pdfFile;

    // Getters and Setters
    public String getGazettePart() {
        return gazettePart;
    }

    public void setGazettePart(String gazettePart) {
        this.gazettePart = gazettePart;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }
}
