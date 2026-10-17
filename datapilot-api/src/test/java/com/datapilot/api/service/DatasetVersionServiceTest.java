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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DatasetVersionServiceTest {

    @Mock
    private DatasetVersionRepository datasetVersionRepository;

    @Mock
    private DatasetRepository datasetRepository;

    @InjectMocks
    private DatasetVersionService datasetVersionService;

    // If this dataset exists and has no previous versions, does my service create version 1 correctly?
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

        // Step 5: Create the API request: This is not a mock. It's a real object.
        //         We're creating the request that a client might send:
        DatasetVersionCreateRequest request = new DatasetVersionCreateRequest();
        request.setFileName("customers_2026_10_07.csv");

        // Step 6:
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

        /* Step 7: Now test the actual method
        Internally
            1. dataset check happens -> Mockito return fake dataset
            2. Version check happens in Db -> Mockito returns Optional.empty(), so the service calculates nextVersion = 1.
            3. Then your service creates:
                DatasetVersion
                ---------------------------
                dataset        = fake dataset
                versionNumber  = 1
                fileName       = customers_2026_10_07.csv
                status         = CREATED
            4.  Then service calls save -> Mockito returns the same version object because of our thenAnswer().
            5.  Now the actual methods get called and it returns DatasetVersionResponse.
        */
        DatasetVersionResponse response = datasetVersionService.createVersion(datasetId, request);

        //
        assertEquals(1, response.getVersionNumber());
        assertEquals("customers_2026_10_07.csv", response.getFileName());
        assertEquals(DatasetVersionStatus.CREATED, response.getStatus());

        // Step : We want to inspect the exact object that the service passed to the repository. Think of ArgumentCaptor as: A container that catches an argument passed to a mock.
        ArgumentCaptor<DatasetVersion> captor =
                ArgumentCaptor.forClass(DatasetVersion.class);

        // Verify that save() was called, and capture the DatasetVersion object that was passed to it. The captor catches that exact object.
        verify(datasetVersionRepository).save(captor.capture());

        // Get the captured object
        // response savedVersion is the actual DatasetVersion object that your service gave to the repository.
        DatasetVersion savedVersion = captor.getValue();

        // Verify the saved object's properties
        assertEquals(1, savedVersion.getVersionNumber());
        assertEquals( "customers_2026_10_07.csv", savedVersion.getFileName());
        assertEquals(DatasetVersionStatus.CREATED, savedVersion.getStatus());

        /*Why both response assertions and ArgumentCaptor?
            We are checking two different things.

        Response assertions :   assertEquals(1, response.getVersionNumber());
        verify:  What did the service return to the caller?

        Captor assertions :   assertEquals(1, savedVersion.getVersionNumber());
        verify:   What did the service try to save?

        So:

                    DatasetVersionService
                               |
                +--------------+--------------+
                |                             |
                ↓                             ↓
            Saved entity                    Response DTO
                |                             |
                ↓                             ↓
             Repository                     Controller

        We're checking both sides.*/
    }

    // Test second version creation
    @Test
    void shouldCreateNextVersion(){
        UUID datasetId = UUID.randomUUID();
        Dataset dataset = mock(Dataset.class);
        when(datasetRepository.findById(datasetId))
                .thenReturn(Optional.of(dataset));

        DatasetVersion latestVersion = new DatasetVersion();
        latestVersion.setDataset(dataset);
        latestVersion.setVersionNumber(1);
        when(datasetVersionRepository.findTopByDatasetIdOrderByVersionNumberDesc(datasetId))
                .thenReturn(Optional.of(latestVersion));

        DatasetVersionCreateRequest request = new DatasetVersionCreateRequest();
        request.setFileName("customers_2026_10_10.csv");
        when(datasetVersionRepository.save(any(DatasetVersion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DatasetVersionResponse response = datasetVersionService
                    .createVersion(datasetId, request);

        assertEquals(2, response.getVersionNumber());
        assertEquals("customers_2026_10_10.csv", response.getFileName());

        verify(datasetVersionRepository)
                .save(any(DatasetVersion.class));
    }

    // Test getting a specific version
    @Test
    void shouldGetVersion(){
        UUID datasetId = UUID.randomUUID();
        Dataset dataset = mock(Dataset.class);
        // Since we're using a mocked Dataset, we need to tell Mockito what getId() should return.
        when(dataset.getId()).thenReturn(datasetId);

        DatasetVersion datasetVersion = new DatasetVersion();
        datasetVersion.setVersionNumber(2);
        datasetVersion.setFileName("customers_v2.csv");
        datasetVersion.setStatus(DatasetVersionStatus.READY);
        datasetVersion.setDataset(dataset);

        when(datasetRepository.findById(datasetId)).thenReturn(Optional.of(dataset));

        when(datasetVersionRepository.findByDatasetIdAndVersionNumber(datasetId, 2))
                .thenReturn(Optional.of(datasetVersion));

        DatasetVersionResponse response = datasetVersionService.getVersion(datasetId, 2);

        assertEquals(datasetId, response.getDatasetId());
        assertEquals(2, response.getVersionNumber());
        assertEquals("customers_v2.csv", response.getFileName());
        assertEquals(DatasetVersionStatus.READY, response.getStatus());

        verify(datasetVersionRepository)
                .findByDatasetIdAndVersionNumber(datasetId, 2);
    }

    // Test nonexistent version
    @Test
    void shouldThrowExceptionWhenVersionDoesNotExist(){
        UUID datasetId = UUID.randomUUID();
        Dataset dataset = mock(Dataset.class);
        when(datasetRepository.findById(datasetId))
                .thenReturn(Optional.of(dataset));

        when(datasetVersionRepository
                .findByDatasetIdAndVersionNumber(datasetId, 99))
                .thenReturn(Optional.empty());

        DatasetVersionNotFoundException exception =
                assertThrows(
                        DatasetVersionNotFoundException.class,
                        () -> datasetVersionService.getVersion(datasetId, 99)
                );

        assertTrue(exception.getMessage().contains("99"));
        verify(datasetVersionRepository)
                .findByDatasetIdAndVersionNumber(datasetId, 99);
    }

    /*    Test nonexistent Dataset
    If the parent Dataset does not exist, we must reject the version creation and must not attempt to save a DatasetVersion.
                Dataset doesn't exist
                   ↓
                404/exception
                   ↓
            Don't even try to create DatasetVersion
     */
    @Test
    void shouldThrowExceptionWhenDatasetDoesNotExist(){
        UUID datasetId = UUID.randomUUID();

        when(datasetRepository.findById(datasetId))
                .thenReturn(Optional.empty());

        DatasetVersionCreateRequest request = new DatasetVersionCreateRequest();
        request.setFileName("customers.csv");

        // Execute this service method and check whether it throws DatasetNotFoundException
        DatasetNotFoundException exception =
                assertThrows(DatasetNotFoundException.class,
                        () -> datasetVersionService.createVersion(datasetId, request));

        assertTrue(exception.getMessage().contains(datasetId.toString()));

        /* Verify that version lookup never happened

            This checks an important detail about the execution flow.
            Your service should check whether the parent Dataset exists before looking up the latest version.
            Since the Dataset wasn't found, the service should stop immediately.
            This assertion verifies that the repository was never asked for the latest version number.
         */
        verify(datasetVersionRepository, never())
                .findTopByDatasetIdOrderByVersionNumberDesc(datasetId);

        /*
            Here's what the code means:
                verify(...) asks Mockito to verify an interaction.
                never() means the interaction must have happened zero times.
                save(...) is the repository operation we're checking.
                any(DatasetVersion.class) matches any DatasetVersion object.
            ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            Dataset doesn't exist
                     |
                     v
            Throw DatasetNotFoundException
                     |
                     v
            Do not calculate next version
                     |
                     v
            Do not save DatasetVersion
         */
        verify(datasetVersionRepository, never())
                .save(any(DatasetVersion.class));
    }
}

