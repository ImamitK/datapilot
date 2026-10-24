package com.datapilot.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageDirectory;

    public FileStorageService(
            @Value("${datapilot.storage.directory:./datapilot-uploads}")
            String storageDirectory){
        this.storageDirectory = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();
    }

    public String store(UUID datasetid, MultipartFile file){
        try {
            Files.createDirectories(storageDirectory);

            // Keep uploaded files separate by dataset.
            Path datasetDirectory = storageDirectory
                    .resolve(datasetid.toString())
                    .normalize();

            if(!datasetDirectory.startsWith(storageDirectory)){
                throw new IllegalArgumentException("Invalid storage path");
            }

            Files.createDirectories(datasetDirectory);

            // Generate the stored name rather than trusting the client filename.
            String extension = getAllowedExtension(file.getOriginalFilename());
            String storedFileName = UUID.randomUUID() + extension;

            Path target = datasetDirectory.resolve(storedFileName);

            Files.copy(file.getInputStream(), target,
                    StandardCopyOption.REPLACE_EXISTING);

            return target.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded file", e);
        }
    }

    private  String getAllowedExtension(String originalFileName){
        if(originalFileName == null || originalFileName.isBlank()){
            throw new IllegalArgumentException("File name is required");
        }

        String name = Paths.get(originalFileName)
                .getFileName()
                .toString();

        int dotIndex = name.lastIndexOf('.');
        if(dotIndex < 0){
            throw new IllegalArgumentException("Only CSV and JSON files are supported");
        }

        String extension = name.substring(dotIndex).toLowerCase();

        if(!extension.equals(".csv") && !extension.equals(".json")){
            throw new IllegalArgumentException("Only CSV and JSON files are supported");
        }
        return extension;
    }
}
