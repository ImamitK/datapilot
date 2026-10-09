package com.datapilot.api.service;

import com.datapilot.api.dto.DatasetVersionCreateRequest;
import com.datapilot.api.dto.DatasetVersionResponse;
import com.datapilot.api.entity.Dataset;
import com.datapilot.api.entity.DatasetVersion;
import com.datapilot.api.entity.DatasetVersionStatus;
import com.datapilot.api.exception.DatasetNotFoundException;
import com.datapilot.api.exception.DatasetVersionNotFoundException;
import com.datapilot.api.repository.DatasetRepository;
import com.datapilot.api.repository.DatasetVersionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DatasetVersionService {

    private final DatasetRepository datasetRepository;
    private final DatasetVersionRepository datasetVersionRepository;

    public DatasetVersionService(DatasetRepository datasetRepository, DatasetVersionRepository datasetVersionRepository) {
        this.datasetRepository = datasetRepository;
        this.datasetVersionRepository = datasetVersionRepository;
    }

    public DatasetVersionResponse createVersion(UUID datasetId, DatasetVersionCreateRequest request){

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

        DatasetVersion version = new DatasetVersion();
        version.setDataset(dataset);
        version.setVersionNumber(nextVersion);
        version.setFileName(request.getFileName());
        version.setStatus(DatasetVersionStatus.CREATED);
        version.setCreatedAt(Instant.now());

        DatasetVersion savedVersion = datasetVersionRepository.save(version);

        return toResponse(savedVersion);
    }

    public List<DatasetVersionResponse> getAllVersions(UUID datasetId) {

        // Does the parent Dataset exist?
        Dataset dataset = datasetRepository
                .findById(datasetId)
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
