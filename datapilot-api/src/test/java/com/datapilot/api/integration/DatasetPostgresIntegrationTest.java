package com.datapilot.api.integration;

import com.datapilot.api.entity.Dataset;
import com.datapilot.api.entity.DatasetStatus;
import com.datapilot.api.entity.SourceType;
import com.datapilot.api.repository.DatasetRepository;
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

    //@Test
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

}
