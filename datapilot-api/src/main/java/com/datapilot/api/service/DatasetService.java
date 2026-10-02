package com.datapilot.api.service;

import com.datapilot.api.dto.DatasetCreateRequest;
import com.datapilot.api.dto.DatasetResponse;
import com.datapilot.api.entity.Dataset;
import com.datapilot.api.entity.DatasetStatus;
import com.datapilot.api.repository.DatasetRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;

    public DatasetService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    /*
    * But there's a problem : We're currently passing our JPA entity directly through the API.
    * For example, our controller could receive:

        {
            "name": "customers",
            "description": "Customer master data",
            "sourceType": "CSV",
            "status": "ACTIVE"
        }

        and convert it directly into: Dataset
        It works.: But I don't want us to do that in the final architecture.
        * Why? Because our database entity and our public API contract are different things. Imagine tomorrow we add:
            internalProcessingStatus
            createdBy
            storageLocation
            embeddingStatus
        to Dataset.
        *
        * We don't necessarily want clients to be able to send those fields.    So we'll introduce:
            DTO
    * */
    public DatasetResponse createDataset(DatasetCreateRequest request){
        Dataset dataset = new Dataset();

        dataset.setName(request.getName());
        dataset.setDescription(request.getDescription());
        dataset.setSourceType(request.getSourceType());

        dataset.setStatus(DatasetStatus.ACTIVE);
        dataset.setCreatedAt(Instant.now());
        dataset.setUpdatedAt(Instant.now());
        return toResponse(datasetRepository.save(dataset));
    }

    public List<DatasetResponse> getDataset() {
        List<Dataset> datasetList = datasetRepository.findAll();
        List<DatasetResponse> datasetResponseList= new ArrayList<>();
        for(Dataset dataset : datasetList){
            datasetResponseList.add(toResponse(dataset));
        }
        return datasetResponseList;

        //Better: return datasetRepository.findAll()
        //        .stream()
        //        .map(this::toResponse)
        //        .toList();
    }

    public DatasetResponse getDataById(UUID id) {
        return datasetRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Dataset not found: "+ id));
    }

    private DatasetResponse toResponse(Dataset dataset) {

        DatasetResponse response = new DatasetResponse();

        response.setId(dataset.getId());
        response.setName(dataset.getName());
        response.setDescription(dataset.getDescription());
        response.setSourceType(dataset.getSourceType());
        response.setStatus(dataset.getStatus());
        response.setCreatedAt(dataset.getCreatedAt());
        response.setUpdatedAt(dataset.getUpdatedAt());

        return response;
    }
}
