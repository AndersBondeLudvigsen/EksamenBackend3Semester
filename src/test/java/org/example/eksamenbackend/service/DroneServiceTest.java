package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.DroneRequestDTO;
import org.example.eksamenbackend.dto.DroneResponseDTO;
import org.example.eksamenbackend.exception.InvalidOperationException;
import org.example.eksamenbackend.exception.ResourceNotFoundException;
import org.example.eksamenbackend.model.Drone;
import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.model.Station;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DroneServiceTest {

    @InjectMocks
    private DroneService droneService;

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private StationRepository stationRepository;

    private Drone drone;
    private Station station;

    @BeforeEach
    void setUp() {
        // Create a sample Station
        station = Station.builder()
                .stationId(1)
                .latitude(55.41)
                .longitude(12.34)
                .build();
        // Initialize the station’s drone list (assume no drones are assigned yet)

        // Create a sample Drone associated with that Station
        drone = Drone.builder()
                .droneId(1)  // Use id "1" for consistency in tests
                .serialUuid("DRONE123")
                .status(DroneStatus.OPERATIONAL)
                .station(station)
                .build();

        // Set up the repository mocks:
        Mockito.when(droneRepository.findAll()).thenReturn(List.of(drone));
        Mockito.when(droneRepository.findById(1)).thenReturn(Optional.of(drone));
        Mockito.when(droneRepository.save(Mockito.any(Drone.class))).thenReturn(drone);
        Mockito.when(stationRepository.findAll()).thenReturn(List.of(station));
    }

    @Test
    void getAllDrones() {
        List<DroneResponseDTO> drones = droneService.getAllDrones();
        assertEquals(1, drones.size());
        DroneResponseDTO dto = drones.get(0);
        assertEquals("DRONE123", dto.serialUuid());
        assertEquals(DroneStatus.OPERATIONAL, dto.status());
        assertNotNull(dto.station());
        assertEquals(1, dto.station().stationId());
    }

    @Test
    void getDroneById_found() {
        DroneResponseDTO response = droneService.getDroneById(1);
        assertNotNull(response);
        assertEquals("DRONE123", response.serialUuid());
        assertEquals(DroneStatus.OPERATIONAL, response.status());
        // Depending on your entity mapping, droneId may be auto-generated; here we assume it is 1.
        assertEquals(1, response.droneId());
    }

    @Test
    void getDroneById_notFound() {
        Mockito.when(droneRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> droneService.getDroneById(999));
    }


    @Test
    void changeDroneStatus_success() {
        DroneResponseDTO response = droneService.changeDroneStatus(1, DroneStatus.OUT_OF_SERVICE);
        assertEquals(DroneStatus.OUT_OF_SERVICE, response.status());
    }

    @Test
    void changeDroneStatus_notFound() {
        Mockito.when(droneRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(InvalidOperationException.class, () -> droneService.changeDroneStatus(999, DroneStatus.OPERATIONAL));
    }
}
