package com.datapilot.api.controller;

import com.datapilot.api.dto.DatasetVersionCreateRequest;
import com.datapilot.api.dto.DatasetVersionResponse;
import com.datapilot.api.entity.DatasetVersionStatus;
import com.datapilot.api.service.DatasetVersionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasetVersionController.class)
public class DatasetVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private DatasetVersionService datasetVersionService;

    //Create DatasetVersion API returns 201 Created with the correct response data
    /*
        Mock HTTP POST request
              |
              v
        DatasetVersionController
              |
              v
        Mock DatasetVersionService
              |
              v
        Return a prepared DatasetVersionResponse
              |
              v
        Verify HTTP status and JSON fields
     */
    @Test
    void shouldCreateVersion() throws Exception {
        UUID datasetId = UUID.randomUUID();

        DatasetVersionResponse response = createResponse(datasetId, 1, "customer_v1.csv");

        when(datasetVersionService
                .createVersion(
                        eq(datasetId),  // Means the first argument must equal our test's datasetId.
                        any(DatasetVersionCreateRequest.class)))  // Means the second argument can be any instance of DatasetVersionCreateRequest because we already have created the response so don't have to worry about request.
                .thenReturn(response);  // Means Mockito returns the prepared response instead of executing the real service logic

        mockMvc.perform(post("/api/v1/datasets/{datasetId}/versions", datasetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "fileName" : "customer_v1.csv"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.datasetId")
                        .value(datasetId.toString()))
                .andExpect(jsonPath("$.versionNumber").value(1))
                .andExpect(jsonPath("$.fileName").value("customer_v1.csv"))
                .andExpect(jsonPath("$.status").value("CREATED"));

    }

    @Test
    void shouldGetAllVersions() throws Exception {
        UUID datasetId = UUID.randomUUID();

        DatasetVersionResponse version2 = createResponse(datasetId, 2, "customer_v2.csv");
        DatasetVersionResponse version1 = createResponse(datasetId, 1, "customer_v1.csv");

        when(datasetVersionService.getAllVersions(datasetId))
                .thenReturn(List.of(version2,version1));

        mockMvc.perform(get("/api/v1/datasets/{datasetId}/versions", datasetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].versionNumber").value(2))
                .andExpect(jsonPath("$[1].versionNumber").value(1));
    }

    @Test
    void shouldGetSpecificVersion() throws Exception {

        UUID datasetId = UUID.randomUUID();

        DatasetVersionResponse response =
                createResponse(datasetId, 2, "customers_v2.csv");

        when(datasetVersionService.getVersion(datasetId, 2))
                .thenReturn(response);

        mockMvc.perform(get(
                        "/api/v1/datasets/{datasetId}/versions/{versionNumber}",
                        datasetId, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versionNumber").value(2))
                .andExpect(jsonPath("$.fileName")
                        .value("customers_v2.csv"));
    }

    @Test
    void shouldReturn400WhenFileNameIsBlank() throws Exception{
        UUID datasetId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/datasets/{datasetId}/versions", datasetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            "fileName" : "  "
                        """))
                .andExpect(status().isBadRequest());
    }

    private DatasetVersionResponse createResponse(UUID datasetId, int versionNumber, String fileName) {

        DatasetVersionResponse response = new DatasetVersionResponse();
        response.setId(datasetId);
        response.setDatasetId(datasetId);
        response.setVersionNumber(versionNumber);
        response.setFileName(fileName);
        response.setStatus(DatasetVersionStatus.CREATED);
        response.setCreatedAt(Instant.now());

        return response;
    }

}
