package com.datapilot.api.service;

import com.datapilot.api.dto.DatasetVersionResponse;
import com.datapilot.api.entity.Dataset;
import com.datapilot.api.entity.DatasetVersion;
import com.datapilot.api.entity.DatasetVersionStatus;
import com.datapilot.api.events.DatasetVersionCreated;
import com.datapilot.api.exception.DatasetNotFoundException;
import com.datapilot.api.exception.DatasetVersionNotFoundException;
import com.datapilot.api.messaging.DatasetVersionEventPublisher;
import com.datapilot.api.repository.DatasetRepository;
import com.datapilot.api.repository.DatasetVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

@Service
public class DatasetVersionService {

    private final DatasetRepository datasetRepository;
    private final DatasetVersionRepository datasetVersionRepository;
    private final FileStorageService fileStorageService;
    private final DatasetVersionEventPublisher datasetVersionEventPublisher;

    public DatasetVersionService(
            DatasetRepository datasetRepository,
            DatasetVersionRepository datasetVersionRepository,
            FileStorageService fileStorageService,
            DatasetVersionEventPublisher datasetVersionEventPublisher) {
        this.datasetRepository = datasetRepository;
        this.datasetVersionRepository = datasetVersionRepository;
        this.fileStorageService = fileStorageService;
        this.datasetVersionEventPublisher = datasetVersionEventPublisher;
    }

    public DatasetVersionResponse createVersion(UUID datasetId, MultipartFile file){

        if (file == null || file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file must not be empty");
        }

        String originalFileName = file.getOriginalFilename();

        if(originalFileName == null || originalFileName.isBlank()){
            throw new IllegalArgumentException("File name is required");
        }

        String normalizedFileName = Path.of(originalFileName)
                .getFileName()
                .toString();

        String lowercaseFileName = normalizedFileName.toLowerCase(Locale.ROOT);

        if(!lowercaseFileName.endsWith(".csv") && !lowercaseFileName.endsWith(".json")){
            throw new IllegalArgumentException("Only CSV and JSON files are supported");
        }

        // Does the parent Dataset exist?
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() ->
                        new DatasetNotFoundException(
                                "Dataset not found: " + datasetId));

        // What is the latest version for this Dataset?
        // One thing we're deliberately NOT solving yet : concurrency race  -> database constraint UNIQUE(dataset_id, version_number) protect the database now
        //TODO Will need retry/locking strategy
        int nextVersion = datasetVersionRepository
                .findTopByDatasetIdOrderByVersionNumberDesc(datasetId)
                .map(version -> version.getVersionNumber()+1)
                .orElse(1);

        String storageLocation = fileStorageService.store(datasetId, file);

        DatasetVersion savedVersion;
        try{
            DatasetVersion version = new DatasetVersion();
            version.setDataset(dataset);
            version.setVersionNumber(nextVersion);
            version.setFileName(normalizedFileName);
            version.setStorageLocation(storageLocation);
            version.setStatus(DatasetVersionStatus.CREATED);
            version.setCreatedAt(Instant.now());

            savedVersion = datasetVersionRepository.save(version);

        } catch (RuntimeException e) {
            // Avoid leaving a file behind when database persistence fails.
            try {
                Files.deleteIfExists(Path.of(storageLocation));
            } catch (IOException cleanupException) {
                e.addSuppressed(cleanupException);
            }
            throw e;
        }

        // Publish only after the version has been persisted.
        DatasetVersionCreated event = DatasetVersionCreated.from(savedVersion);
        datasetVersionEventPublisher.publish(event);

        return toResponse(savedVersion);
    }

    public List<DatasetVersionResponse> getAllVersions(UUID datasetId) {

        // Does the parent Dataset exist?
        Dataset dataset = datasetRepository.findById(datasetId)
                .orElseThrow(() ->
                        new DatasetNotFoundException(
                                "Dataset not found: " + datasetId));

        List<DatasetVersion> versions = datasetVersionRepository
                .findByDatasetIdOrderByVersionNumberDesc(datasetId);

        List<DatasetVersionResponse> response = new ArrayList<>();
        for(DatasetVersion version : versions){
            response.add(toResponse(version));
        }
        return response;

       /* return datasetVersionRepository
                .findByDatasetIdOrderByVersionNumberDesc(datasetId)
                .stream()
                .map(this::toResponse)
                .toList();*/
    }

    public DatasetVersionResponse getVersion(UUID datasetId, Integer versionNumber) {

        // Does the parent Dataset exist?
        Dataset dataset = datasetRepository
                .findById(datasetId)
                .orElseThrow(() ->
                        new DatasetNotFoundException(
                                "Dataset not found: " + datasetId));

        DatasetVersion version = datasetVersionRepository
                .findByDatasetIdAndVersionNumber(datasetId, versionNumber)
                .orElseThrow(() ->
                        new DatasetVersionNotFoundException(
                                "Dataset version not found " + datasetId + " / v" + versionNumber));
        return toResponse(version);
    }

    private DatasetVersionResponse toResponse(DatasetVersion version) {
        DatasetVersionResponse response = new DatasetVersionResponse();

        response.setId(version.getId());
        response.setDatasetId(version.getDataset().getId());
        response.setVersionNumber(version.getVersionNumber());
        response.setFileName(version.getFileName());
        response.setStorageLocation(version.getStorageLocation());
        response.setRowCount(version.getRowCount());
        response.setStatus(version.getStatus());
        response.setCreatedAt(version.getCreatedAt());

        return response;
    }
}
