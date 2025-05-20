package spring.aop.gazettemanagementnic.dto;

import spring.aop.gazettemanagementnic.entity.Pdf;

public class PdfWithSize {
    private Pdf pdf;
    private String size;

    public PdfWithSize(Pdf pdf, String size) {
        this.pdf = pdf;
        this.size = size;
    }

    public Pdf getPdf() {
        return pdf;
    }

    public String getSize() {
        return size;
    }
}

