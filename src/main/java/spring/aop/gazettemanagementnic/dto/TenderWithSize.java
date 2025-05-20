package spring.aop.gazettemanagementnic.dto;

import spring.aop.gazettemanagementnic.entity.Tender;

public class TenderWithSize {
    private Tender tender;
    private String fileSize;

    public TenderWithSize(Tender tender, String fileSize) {
        this.tender = tender;
        this.fileSize = fileSize;
    }

    public Tender getTender() {
        return tender;
    }

    public String getFileSize() {
        return fileSize;
    }
}
