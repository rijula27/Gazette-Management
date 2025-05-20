package spring.aop.gazettemanagementnic.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import spring.aop.gazettemanagementnic.entity.Pdf;
import java.util.List;



@Repository
public interface PdfRepository extends JpaRepository<Pdf, Long> {

    boolean existsByPdfTitle(String pdfTitle);

}

