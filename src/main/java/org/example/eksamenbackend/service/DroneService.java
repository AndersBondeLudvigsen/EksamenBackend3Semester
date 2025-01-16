package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.DroneRequestDTO;
import org.example.eksamenbackend.dto.DroneResponseDTO;
import org.example.eksamenbackend.dto.StationDTO;
import org.example.eksamenbackend.model.Drone;
import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.model.Station;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class DroneService {

    private final DroneRepository droneRepository;
    private final StationRepository stationRepository;

    // Constructor injection
    public DroneService(DroneRepository droneRepository, StationRepository stationRepository) {
        this.droneRepository = droneRepository;
        this.stationRepository = stationRepository;
    }

    // ------------------------------------------------------------------------
    // DroneService Methods
    // ------------------------------------------------------------------------

    public List<DroneResponseDTO> getAllDrones() {
        return droneRepository.findAll().stream()
                .map(this::mapToDroneResponseDTO) // Reuse helper method
                .toList();
    }

    public DroneResponseDTO getDroneById(int id) {
        return droneRepository.findById(id)
                .map(this::mapToDroneResponseDTO) // Reuse helper method
                .orElseThrow(() -> new RuntimeException("Drone with id " + id + " not found"));
    }

    public DroneResponseDTO addDrone(DroneRequestDTO requestDTO) {
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            throw new RuntimeException("No stations available to assign the drone.");
        }

        Station stationWithFewestDrones = stations.stream()
                .min(Comparator.comparingInt(station -> station.getDroner().size()))
                .orElseThrow();

        Drone newDrone = Drone.builder()
                .serialUuid(requestDTO.serialUuid())
                .status(requestDTO.status())
                .station(stationWithFewestDrones)
                .build();

        Drone savedDrone = droneRepository.save(newDrone);

        return mapToDroneResponseDTO(savedDrone); // Reuse helper method
    }



    public DroneResponseDTO changeDroneStatus(int id, DroneStatus status) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone with id " + id + " not found"));

        drone.setStatus(status);
        Drone updatedDrone = droneRepository.save(drone);

        return mapToDroneResponseDTO(updatedDrone); // Reuse helper method
    }

    // ------------------------------------------------------------------------
    // Helper Methods for DTO Mapping
    // ------------------------------------------------------------------------

    private DroneResponseDTO mapToDroneResponseDTO(Drone drone) {
        return new DroneResponseDTO(
                drone.getSerialUuid(),
                drone.getStatus(),
                mapToStationDTO(drone.getStation()), // Reuse station mapping
                drone.getDroneId()
        );
    }

    private StationDTO mapToStationDTO(Station station) {
        if (station == null) {
            return null;
        }
        return new StationDTO(
                station.getStationId(),
                station.getLatitude(),
                station.getLongitude()
        );
    }
}
