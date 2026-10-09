package com.datapilot.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DatasetVersionCreateRequest {

    @NotBlank(message="fileName is required")
    @Size(max=255, message="fileName must not exceed 255 characters")
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
