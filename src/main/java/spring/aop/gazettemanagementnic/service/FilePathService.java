package spring.aop.gazettemanagementnic.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import spring.aop.gazettemanagementnic.entity.FilePath;
import spring.aop.gazettemanagementnic.repository.FilePathRepository;

@Service
@RequiredArgsConstructor
public class FilePathService {

    private final FilePathRepository filePathRepository;

    public Optional<FilePath> getFilePathByDescription(String description) {
        return filePathRepository.findByPathDescription(description);
    }

}
