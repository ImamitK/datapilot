package com.datapilot.api.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;


/*
PRIMARY KEY
    dataset_versions_pkey
        ↓
    id

FOREIGN KEY
    fk_dataset_version_dataset
        ↓
    dataset_id → datasets.id

UNIQUE
    uk_dataset_version
        ↓
    (dataset_id, version_number)
 */

@Entity
@Table(
        name = "dataset_versions",
        uniqueConstraints = {
                @UniqueConstraint( // uniqueConstraints tells the database that a particular combination of column values must be unique. For a given dataset, a version number can exist only once.
                    name = "uk_dataset_version", columnNames = {"dataset_id", "version_number"}
                )
        }
)
public class DatasetVersion {

    @Id
    @GeneratedValue
    private UUID id;

    //Many DatasetVersion records can belong to one Dataset.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "dataset_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_dataset_version_dataset")
    )
    private Dataset dataset;  //did not put datasetId as a plain UUID field like: Because we're modeling the domain relationship, not just copying a database column into Java, JPA then maps that relationship to: dataset_id in PostgreSQL.

    @Column(name="version_number", nullable = false)
    private Integer versionNumber;

    @Column(nullable = false)
    private String fileName;

    private String storageLocation;

    private Long rowCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DatasetVersionStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    public UUID getId(){
        return id;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public void setRowCount(Long rowCount) {
        this.rowCount = rowCount;
    }

    public DatasetVersionStatus getStatus() {
        return status;
    }

    public void setStatus(DatasetVersionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
