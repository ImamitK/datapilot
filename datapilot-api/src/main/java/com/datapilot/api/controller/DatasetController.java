package com.datapilot.api.controller;

import com.datapilot.api.dto.DatasetCreateRequest;
import com.datapilot.api.dto.DatasetResponse;
import com.datapilot.api.entity.Dataset;
import com.datapilot.api.repository.DatasetRepository;
import com.datapilot.api.service.DatasetService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService){
        this.datasetService = datasetService;
    }

    @PostMapping
    public DatasetResponse createDataset(@RequestBody DatasetCreateRequest request){
        return datasetService.createDataset(request);
    }

    @GetMapping
    public List<DatasetResponse> getDataset(){
        return  datasetService.getDataset();
    }

    @GetMapping("{id}")
    public DatasetResponse getDatasetById(@RequestAttribute UUID uuid){
        return datasetService.getDataById(uuid);
    }


}
