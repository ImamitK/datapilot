package com.datapilot.api.events;

import com.datapilot.api.entity.DatasetVersion;

import java.time.Instant;
import java.util.UUID;

public record DatasetVersionCreated(
        UUID eventId,
        UUID datasetId,
        UUID datasetVersionId,
        Integer versionNumber,
        String fileName,
        String storageLocation,
        Instant occurredAt
) {
    /* The from() method converts our persisted JPA entity into an independent event object.
    We don't send the JPA entity itself, because the event should be a stable message contract, not a representation of the database object.*/
    public static DatasetVersionCreated from(DatasetVersion version) {

        return new DatasetVersionCreated(
                UUID.randomUUID(),
                version.getDataset().getId(),
                version.getId(),
                version.getVersionNumber(),
                version.getFileName(),
                version.getStorageLocation(),
                Instant.now()
        );
    }
}
