package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.DroneRequestDTO;
import org.example.eksamenbackend.dto.DroneResponseDTO;
import org.example.eksamenbackend.dto.StationDTO;
import org.example.eksamenbackend.exception.InvalidOperationException;
import org.example.eksamenbackend.exception.ResourceNotFoundException;
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

    public DroneService(DroneRepository droneRepository, StationRepository stationRepository) {
        this.droneRepository = droneRepository;
        this.stationRepository = stationRepository;
    }

    public List<DroneResponseDTO> getAllDrones() {
        return droneRepository.findAll().stream()
                .map(this::mapToDroneResponseDTO)
                .toList();
    }

    public DroneResponseDTO getDroneById(int id) {
        return droneRepository.findById(id)
                .map(this::mapToDroneResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Drone with id " + id + " not found"));
    }

    public DroneResponseDTO addDrone(DroneRequestDTO requestDTO) {
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            throw new InvalidOperationException("No stations available to assign the drone.");
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

        return mapToDroneResponseDTO(savedDrone);
    }



    public DroneResponseDTO changeDroneStatus(int id, DroneStatus status) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new InvalidOperationException("Drone with id " + id + " not found"));

        drone.setStatus(status);
        Drone updatedDrone = droneRepository.save(drone);

        return mapToDroneResponseDTO(updatedDrone);
    }


    private DroneResponseDTO mapToDroneResponseDTO(Drone drone) {
        return new DroneResponseDTO(
                drone.getSerialUuid(),
                drone.getStatus(),
                mapToStationDTO(drone.getStation()),
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
