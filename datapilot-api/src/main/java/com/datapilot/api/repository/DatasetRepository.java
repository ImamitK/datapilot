package com.datapilot.api.repository;

import com.datapilot.api.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/*
* "Did you implement DatasetRepository?"
     "No. I extended Spring Data JPA's JpaRepository. Spring creates the repository implementation at runtime
     * using Spring Data's proxy mechanism, and Hibernate handles the ORM and SQL generation."
     *
Your code
   │
   ▼
DatasetRepository
   │
   ▼
Spring Data JPA
   │
   ▼
Hibernate
   │
   ▼
JDBC
   │
   ▼
PostgreSQL
* */
public interface DatasetRepository extends JpaRepository<Dataset, UUID> {

    boolean existsByName(String name);
}
