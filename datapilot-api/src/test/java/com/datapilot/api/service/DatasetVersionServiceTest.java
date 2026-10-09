package com.datapilot.api.service;

import com.datapilot.api.dto.DatasetVersionCreateRequest;
import com.datapilot.api.entity.Dataset;
import com.datapilot.api.entity.DatasetVersion;
import com.datapilot.api.repository.DatasetRepository;
import com.datapilot.api.repository.DatasetVersionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DatasetVersionServiceTest {

    @Mock
    private DatasetVersionRepository datasetVersionRepository;

    @Mock
    private DatasetRepository datasetRepository;

    @InjectMocks
    private DatasetVersionService datasetVersionService;

    // “If this dataset exists and has no previous versions, does my service create version 1 correctly?
    @Test
    void shouldCreateFirstVersion(){

        // Step 1: creating an ID for the dataset
        UUID datasetId = UUID.randomUUID();

        // Step 2: creates a Mockito fake object.  equivalent to real object : Dataset dataset = new Dataset();
        Dataset dataset = mock(Dataset.class);

        /*Step 3: When the service calls datasetRepository.findById(datasetId), pretend the dataset exists and return this fake dataset.
         basic Mockito pattern:
              when(SOMETHING)
                   .thenReturn(SOMETHING_ELSE);*/
        when(datasetRepository.findById(datasetId))
                .thenReturn(Optional.of(dataset));

        // Step 4: Tell Mockito that there are no previous versions : When the service asks for the latest version of this dataset, pretend there isn't one.
        when(datasetVersionRepository.findTopByDatasetIdOrderByVersionNumberDesc(datasetId))
                .thenReturn(Optional.empty());


        DatasetVersionCreateRequest request = new DatasetVersionCreateRequest();
        request.setFileName("customers_2026_10_07.csv");

        // any(DatasetVersion.class)  ->  I don't care which DatasetVersion object is passed to save(). version1 , version2 or any other DatasetVersion
        // .thenAnswer(...)  -> When save() is called, dynamically decide what to return.
        //      invocation
        //          =
        //      the actual method call that happened
        // In real service executes
        //      DatasetVersion version = ...;
        //      datasetVersionRepository.save(version);
        // Mockito sees: save(version)  -> invocation represents that call.
        // invocation -> invocation.getArgument(0) -> Give me the first argument that was passed into the mocked method.
        //
        // when(datasetVersionRepository.save(any(DatasetVersion.class)))
        //        .thenReturn(savedVersion);  -> Whenever save() is called, return this savedVersion object.
        //
        // That's okay for many tests, but it doesn't prove exactly what your service passed to the repository.
        // That's why thenAnswer(invocation -> invocation.getArgument(0)) is useful.
        // In Step 4: service receives: Optional.empty() -> There is no previous version. So .orElse(1) from service we get in returns as 1.
        when(datasetVersionRepository.save(any(DatasetVersion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


    }

}
