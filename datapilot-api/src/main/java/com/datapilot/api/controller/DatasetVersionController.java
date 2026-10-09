package com.datapilot.api.controller;

import com.datapilot.api.dto.DatasetVersionCreateRequest;
import com.datapilot.api.dto.DatasetVersionResponse;
import com.datapilot.api.service.DatasetVersionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets/{datasetId}/versions")
public class DatasetVersionController {

    private final DatasetVersionService datasetVersionService;

    public DatasetVersionController(DatasetVersionService datasetVersionService) {
        this.datasetVersionService = datasetVersionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetVersionResponse createVersion(
            @PathVariable UUID datasetId,
            @Valid @RequestBody DatasetVersionCreateRequest request){
        return datasetVersionService.createVersion(datasetId, request);
    }

    @GetMapping
    public List<DatasetVersionResponse> getAllVersions(
            @PathVariable UUID datasetId){
        return datasetVersionService.getAllVersions(datasetId);
    }

    @GetMapping("/{versionNumber}")
    public DatasetVersionResponse getVersion(
            @PathVariable UUID datasetId,
            @PathVariable Integer versionNumber){
        return datasetVersionService.getVersion(datasetId, versionNumber);
    }
}
