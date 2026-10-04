package com.datapilot.api.controller;

import com.datapilot.api.dto.DatasetCreateRequest;
import com.datapilot.api.dto.DatasetResponse;
import com.datapilot.api.service.DatasetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService){
        this.datasetService = datasetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetResponse createDataset(
            @Valid @RequestBody DatasetCreateRequest request){
        return datasetService.createDataset(request);
    }

    @GetMapping
    public List<DatasetResponse> getDataset(){
        return  datasetService.getDataset();
    }

    @GetMapping("/{id}")
    public DatasetResponse getDatasetById(@PathVariable UUID id){
        return datasetService.getDatasetById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDatasetById(@PathVariable UUID id){
        datasetService.deleteDataset(id);
    }

}
