package com.datapilot.api.dto;

import com.datapilot.api.entity.SourceType;

// "what the client is allowed to send."
public class DatasetCreateRequest {

    private String name;
    private String description;
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
