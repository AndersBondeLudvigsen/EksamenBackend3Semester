package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.dto.LeveringsResponseDTO;
import org.example.eksamenbackend.model.*;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.LeveringsRepository;
import org.example.eksamenbackend.repository.PizzaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LeveringsServiceTest {

    @InjectMocks
    private LeveringsService leveringsService;

    @Mock
    private LeveringsRepository leveringRepository;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private DroneRepository droneRepository;

    private Pizza pizza;
    private Drone drone;
    private Levering levering;

    @BeforeEach
    void setUp() {
        pizza = Pizza.builder()
                .titel("Margherita")
                .build();


        drone = Drone.builder()
                .serialUuid("DRONE123")
                .status(DroneStatus.OPERATIONAL)
                .build();


        levering = Levering.builder()
                .adresse("Testvej 1")
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .pizza(pizza)
                .build();

        Mockito.when(pizzaRepository.findById(1)).thenReturn(Optional.of(pizza));
        Mockito.when(droneRepository.findById(1)).thenReturn(Optional.of(drone));
        Mockito.when(leveringRepository.save(Mockito.any(Levering.class))).thenReturn(levering);
        Mockito.when(leveringRepository.findById(1)).thenReturn(Optional.of(levering));
        Mockito.when(leveringRepository.findAllByFaktiskLeveringIsNull()).thenReturn(List.of(levering));
        Mockito.when(leveringRepository.findAllByDroneIsNull()).thenReturn(List.of(levering));
    }

    @Test
    void getAllPendingDeliveries() {
        List<LeveringsResponseDTO> pendingDeliveries = leveringsService.getAllPendingDeliveries();
        assertEquals(1, pendingDeliveries.size());
        assertEquals("Testvej 1", pendingDeliveries.get(0).adresse());
    }

    @Test
    void addDelivery() {
        LeveringsRequestDTO requestDTO = new LeveringsRequestDTO("Testvej 1", 1);
        LeveringsResponseDTO responseDTO = leveringsService.addDelivery(requestDTO);
        assertEquals("Testvej 1", responseDTO.adresse());
        assertEquals("Margherita", responseDTO.pizzaName());
    }

    @Test
    void getQueuedDeliveries() {
        List<LeveringsResponseDTO> queuedDeliveries = leveringsService.getQueuedDeliveries();
        assertEquals(1, queuedDeliveries.size());
        assertEquals("Testvej 1", queuedDeliveries.get(0).adresse());
    }

    @Test
    void scheduleDelivery() {
        LeveringsResponseDTO responseDTO = leveringsService.scheduleDelivery(1, 1);
        assertEquals("Testvej 1", responseDTO.adresse());
        assertEquals("DRONE123", responseDTO.droneSerialUuid());
    }

    @Test
    void finishDelivery() {
        levering.setDrone(drone);
        LeveringsResponseDTO responseDTO = leveringsService.finishDelivery(1);
        assertNotNull(responseDTO.faktiskLevering());
    }


}
