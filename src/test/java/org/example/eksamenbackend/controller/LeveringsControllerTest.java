package org.example.eksamenbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.dto.LeveringsResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LeveringsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldGetAllPendingDeliveries() throws Exception {
        mockMvc.perform(get("/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldAddDelivery() throws Exception {
        LeveringsRequestDTO request = new LeveringsRequestDTO("Testvej 1", 1);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/deliveries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adresse").value("Testvej 1"));
    }

    @Test
    void shouldGetQueuedDeliveries() throws Exception {
        mockMvc.perform(get("/deliveries/queue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }



    @Test
    void shouldFinishDelivery() throws Exception {
        int leveringId = 1;

        mockMvc.perform(post("/deliveries/finish/{leveringId}", leveringId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.faktiskLevering").exists());
    }

    @Test
    void shouldSimulateAddDelivery() throws Exception {
        LeveringsRequestDTO request = new LeveringsRequestDTO("Simulatevej 2", 1);
        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/deliveries/simulate/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adresse").value("Simulatevej 2"));
    }


}
