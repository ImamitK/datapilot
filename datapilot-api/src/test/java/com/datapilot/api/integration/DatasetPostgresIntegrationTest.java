package com.datapilot.api.integration;

import com.datapilot.api.entity.*;
import com.datapilot.api.repository.DatasetRepository;
import com.datapilot.api.repository.DatasetVersionRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@Testcontainers
@SpringBootTest
public class DatasetPostgresIntegrationTest {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine")
                    .withDatabaseName("datapilot")
                    .withUsername("datapilot")
                    .withPassword("datapilot");

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add(
                "spring.jpa.hibernate.ddl-auto",
                () -> "create-drop"
        );
    }

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private DatasetVersionRepository datasetVersionRepository;

    @Test
    void shouldPersistAndReadDatasetFromPostgres() {

        Dataset dataset = new Dataset();

        dataset.setName("customer-data");
        dataset.setDescription("Customer master dataset");
        dataset.setSourceType(SourceType.CSV);
        dataset.setStatus(DatasetStatus.ACTIVE);

        Instant now = Instant.now();
        dataset.setCreatedAt(now);
        dataset.setUpdatedAt(now);

        Dataset savedDataset =
                datasetRepository.saveAndFlush(dataset);

        assertNotNull(savedDataset.getId());

        UUID id = savedDataset.getId();

        Dataset foundDataset =
                datasetRepository.findById(id)
                        .orElseThrow();

        assertEquals("customer-data", foundDataset.getName());
        assertEquals(
                SourceType.CSV,
                foundDataset.getSourceType()
        );
        assertEquals(
                DatasetStatus.ACTIVE,
                foundDataset.getStatus()
        );
    }

    @Test
    @Transactional
    void shouldPersistAndReadDatasetVersionFromPostgres() {
        // Step 1: Create and persist the parent Dataset
        Dataset dataset = new Dataset();
        dataset.setName("customers-" + UUID.randomUUID());
        dataset.setDescription("Customer master data");
        dataset.setSourceType(SourceType.CSV);
        dataset.setStatus(DatasetStatus.ACTIVE);

        Instant now = Instant.now();
        dataset.setCreatedAt(now);
        dataset.setUpdatedAt(now);

        Dataset savedDataset = datasetRepository.saveAndFlush(dataset);

        // Step 2: Create a DatasetVersion referencing that Dataset
        DatasetVersion version = new DatasetVersion();
        version.setDataset(savedDataset);
        version.setVersionNumber(1);
        version.setFileName("customers.csv");
        version.setStorageLocation("integration-test/customers.csv");
        version.setStatus(DatasetVersionStatus.CREATED);
        version.setCreatedAt(now);

        DatasetVersion savedVersion = datasetVersionRepository.saveAndFlush(version);

        // Step 3: Verify PostgreSQL generated the version ID
        assertNotNull(savedVersion);

        // Step 4: Retrieve the version through the real repository
        DatasetVersion foundVersion = datasetVersionRepository
                .findByDatasetIdAndVersionNumber(
                        savedDataset.getId(),
                        1)
                .orElseThrow();

        // Step 5: Verify the persisted data and relationship
        assertEquals(1, foundVersion.getVersionNumber());
        assertEquals("customers.csv", foundVersion.getFileName());
        assertEquals(
                DatasetVersionStatus.CREATED,
                foundVersion.getStatus());
        assertEquals(
                savedDataset.getId(),
                foundVersion.getDataset().getId());
    }
}
