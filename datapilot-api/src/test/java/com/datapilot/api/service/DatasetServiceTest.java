package com.datapilot.api.service;

import com.datapilot.api.dto.DatasetCreateRequest;
import com.datapilot.api.dto.DatasetResponse;
import com.datapilot.api.entity.Dataset;
import com.datapilot.api.entity.DatasetStatus;
import com.datapilot.api.entity.SourceType;
import com.datapilot.api.exception.DatasetAlreadyExistsException;
import com.datapilot.api.exception.DatasetNotFoundException;
import com.datapilot.api.repository.DatasetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatasetServiceTest {

    @Mock
    private DatasetRepository datasetRepository;

    @InjectMocks
    private DatasetService datasetService;

    @Test
    void shouldCreateDatasets(){
        DatasetCreateRequest request = new DatasetCreateRequest();
        request.setName("customer-data");
        request.setDescription("Customer master dataset");
        request.setSourceType(SourceType.CSV);
        when(datasetRepository.existsByName("customer-data"))
                .thenReturn(false);

        Dataset savedDataset = new Dataset();
        savedDataset.setName("customer-data");
        savedDataset.setDescription("Customer master dataset");
        savedDataset.setSourceType(SourceType.CSV);
        savedDataset.setStatus(DatasetStatus.ACTIVE);
        savedDataset.setCreatedAt(Instant.now());
        savedDataset.setUpdatedAt(Instant.now());
        when(datasetRepository.save(any(Dataset.class)))
                .thenReturn(savedDataset);

        DatasetResponse response = datasetService
                .createDataset(request);

        assertEquals("customer-data", response.getName());
        assertEquals(SourceType.CSV, response.getSourceType());
        assertEquals(DatasetStatus.ACTIVE, response.getStatus());

        verify(datasetRepository).save(any(Dataset.class));
    }

    @Test
    void shouldGetDataset(){
        UUID id = UUID.randomUUID();

        Dataset dataset = new Dataset();
        dataset.setName("customer-data");
        dataset.setDescription("Customer master dataset");
        dataset.setSourceType(SourceType.CSV);
        dataset.setStatus(DatasetStatus.ACTIVE);
        dataset.setCreatedAt(Instant.now());
        dataset.setUpdatedAt(Instant.now());

        // When we ask for dataset for id: newly created id return the above fake dataset
        when(datasetRepository.findById(id))
                .thenReturn(Optional.of(dataset));

        DatasetResponse response =
                datasetService.getDatasetById(id);

        assertEquals("customer-data", response.getName());
        assertEquals(SourceType.CSV, response.getSourceType());
        assertEquals(DatasetStatus.ACTIVE, response.getStatus());

        verify(datasetRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenDatasetDoesNotExist(){
        UUID id = UUID.randomUUID();

        when(datasetRepository.findById(id))
                .thenReturn(Optional.empty());

        DatasetNotFoundException exception =
                assertThrows(
                        DatasetNotFoundException.class,
                        () -> datasetService.getDatasetById(id)
                );

        assertTrue(exception.getMessage().contains(id.toString()));

        verify(datasetRepository).findById(id);
    }

    @Test
    void shouldSoftDeleteDataset(){
        UUID id = UUID.randomUUID();

        Dataset dataset = new Dataset();
        dataset.setName("customer-data");
        dataset.setStatus(DatasetStatus.ACTIVE);

        when(datasetRepository.findById(id))
                .thenReturn(Optional.of(dataset));

        datasetService.deleteDataset(id);

        assertEquals(DatasetStatus.INACTIVE, dataset.getStatus());

        verify(datasetRepository).save(dataset);

    }

    @Test
    void shouldThrowExceptionWhenCreatingDuplicateDataset(){
        DatasetCreateRequest request = new DatasetCreateRequest();
        request.setName("customer-data");
        request.setDescription("Customer master dataset");
        request.setSourceType(SourceType.CSV);

        when(datasetRepository.existsByName("customer-data"))
                .thenReturn(true);

        DatasetAlreadyExistsException exception =
                assertThrows(
                        DatasetAlreadyExistsException.class,
                        () -> datasetService.createDataset(request));

        assertEquals("Dataset already exists: customer-data", exception.getMessage());

        verify(datasetRepository, never()).save(any(Dataset.class));
    }

}
