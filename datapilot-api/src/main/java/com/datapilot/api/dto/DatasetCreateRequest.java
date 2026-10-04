package com.datapilot.api.dto;

import com.datapilot.api.entity.SourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// "what the client is allowed to send."
public class DatasetCreateRequest {

    @NotBlank(message = "name is required")
    @Size(max=100, message = "name must not exceed 100 characters")
    private String name;

    @Size(max=500, message = "description must not exceed 500 characters")
    private String description;

    @NotNull(message = "SourceType is required")
    private SourceType sourceType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SourceType getSourceType() {
        return sourceType;
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType;
    }
}
