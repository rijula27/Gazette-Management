package spring.aop.gazettemanagementnic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import spring.aop.gazettemanagementnic.entity.FilePath;



@Repository
public interface FilePathRepository extends JpaRepository<FilePath, Long>{

    Optional<FilePath> findByPathDescription(String pathDescription);

    Optional<FilePath> findByPathId(Long pathId);

}
