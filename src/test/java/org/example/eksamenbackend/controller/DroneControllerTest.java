package org.example.eksamenbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eksamenbackend.dto.DroneRequestDTO;
import org.example.eksamenbackend.dto.DroneResponseDTO;
import org.example.eksamenbackend.model.DroneStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Test for GET /drones
    @Test
    void shouldGetAllDrones() throws Exception {
        mockMvc.perform(get("/drones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }


    @Test
    void shouldReturnNotFoundForNonExistentDrone() throws Exception {
        mockMvc.perform(get("/drones/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldAddDrone() throws Exception {
        DroneRequestDTO request = new DroneRequestDTO("DRONE-XYZ", DroneStatus.OUT_OF_SERVICE, null);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/drones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serialUuid", is("DRONE-XYZ")))
                .andExpect(jsonPath("$.status", is(DroneStatus.OUT_OF_SERVICE.toString())))
                .andExpect(jsonPath("$.station.stationId").exists());
    }

    @Test
    void shouldEnableDrone() throws Exception {
        mockMvc.perform(post("/drones/enable/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(DroneStatus.OPERATIONAL.toString())));
    }

    @Test
    void shouldDisableDrone() throws Exception {
        mockMvc.perform(post("/drones/disable/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(DroneStatus.OUT_OF_SERVICE.toString())));
    }

    @Test
    void shouldRetireDrone() throws Exception {
        mockMvc.perform(post("/drones/retire/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(DroneStatus.RETIRED.toString())));
    }
}
