package spring.aop.gazettemanagementnic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.aop.gazettemanagementnic.entity.ImageGallery;

@Repository
public interface ImageGalleryRepository extends JpaRepository<ImageGallery, Long> {

    ImageGallery findByImageTitle(String imageTitle);
}
