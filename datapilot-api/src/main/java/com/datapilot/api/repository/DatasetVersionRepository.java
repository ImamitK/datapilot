package com.datapilot.api.repository;

import com.datapilot.api.entity.DatasetVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DatasetVersionRepository extends JpaRepository<DatasetVersion, UUID> {

    Optional<DatasetVersion> findByDatasetIdAndVersionNumber(
            UUID datasetUuid,
            Integer versionNumber
    );

    // Find the first DatasetVersion for this dataset, ordered by version number descending.
    /*
    Suppose:
        dataset_id = customers

            version_number
            ---------------
                1
                2
                3

            OrderByVersionNumberDesc gives:

                3
                2
                1

            and Top takes:

                3

            So:

            findTopByDatasetIdOrderByVersionNumberDesc(datasetId)

                returns version 3.
                 */
    Optional<DatasetVersion> findTopByDatasetIdOrderByVersionNumberDesc(
            UUID datasetId
    );

    List<DatasetVersion> findByDatasetIdOrderByVersionNumberDesc(
            UUID datasetId
    );
}
