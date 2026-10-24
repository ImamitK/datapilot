package com.datapilot.api.controller;

import com.datapilot.api.dto.DatasetVersionResponse;
import com.datapilot.api.service.DatasetVersionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets/{datasetId}/versions")
public class DatasetVersionController {

    private final DatasetVersionService datasetVersionService;

    public DatasetVersionController(DatasetVersionService datasetVersionService) {
        this.datasetVersionService = datasetVersionService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetVersionResponse createVersion(
            @PathVariable UUID datasetId,
            @RequestParam("file")MultipartFile file){
        return datasetVersionService.createVersion(datasetId, file);
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
