package com.datapilot.api.controller;

import com.datapilot.api.dto.DatasetResponse;
import com.datapilot.api.entity.DatasetStatus;
import com.datapilot.api.entity.SourceType;
import com.datapilot.api.exception.DatasetNotFoundException;
import com.datapilot.api.service.DatasetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DatasetController.class)
public final class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DatasetService datasetService;

   @Test
   void shouldCreateDataset() throws Exception{
       UUID id = UUID.randomUUID();

       DatasetResponse response = new DatasetResponse();
       response.setId(id);
       response.setName("customer-data");
       response.setDescription("Customer master dataset");
       response.setSourceType(SourceType.CSV);
       response.setStatus(DatasetStatus.ACTIVE);
       response.setCreatedAt(Instant.now());
       response.setUpdatedAt(Instant.now());

       when(datasetService.createDataset(any()))
               .thenReturn(response);

       mockMvc.perform(post("/api/v1/datasets")
               .contentType(APPLICATION_JSON)
               .content("""
                            {
                              "name": "customer-data",
                              "description": "Customer master dataset",
                              "sourceType": "CSV"
                            }
                            """))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(id.toString()))
               .andExpect(jsonPath("$.name").value("customer-data"))
               .andExpect(jsonPath("$.sourceType").value("CSV"))
               .andExpect(jsonPath("$.status").value("ACTIVE"));
   }

   @Test
    void shouldGetDataset() throws Exception{
       DatasetResponse response = new DatasetResponse();
       response.setId(UUID.randomUUID());
       response.setName("customer-data");
       response.setSourceType(SourceType.CSV);
       response.setStatus(DatasetStatus.ACTIVE);

       when(datasetService.getDataset())
               .thenReturn(List.of(response));

       mockMvc.perform(get("/api/v1/datasets"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].name").value("customer-data"))
               .andExpect(jsonPath("$[0].sourceType").value("CSV"));
   }

    /*@Test
    void shouldReturn404WhenDatasetDoesNotExist() throws Exception {

        UUID id = UUID.randomUUID();

        when(datasetService.getDatasetById(id))
                .thenThrow(new DatasetNotFoundException(
                        "Dataset not found: " + id));

        mockMvc.perform(get("/api/v1/datasets/{id}", id))
                .andExpect(status().isNotFound());
    }*/

   @Test
    void shouldReturn400ForInvalidRequest() throws Exception{
       mockMvc.perform(post("/api/v1/datasets")
               .contentType(APPLICATION_JSON)
               .content("""
                      {
                        "name": "",
                        "sourceType": null
                      }
                       """))
               .andExpect(status().isBadRequest());
   }

   @Test
    void shouldDeleteDataset() throws Exception{
       UUID id = UUID.randomUUID();
       mockMvc.perform(delete("/api/v1/datasets/{id}", id))
               .andExpect(status().isNoContent());
   }
}
