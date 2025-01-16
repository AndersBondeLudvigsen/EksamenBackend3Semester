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

    public List<DroneResponseDTO> getAllDrones() {
        return droneRepository.findAll().stream()
                .map(drone -> {
                    Station station = drone.getStation();
                    StationDTO stationDTO = new StationDTO(
                            station.getStationId(),
                            station.getLatitude(),
                            station.getLongitude()
                    );
                    return new DroneResponseDTO(
                            drone.getSerialUuid(),
                            drone.getStatus(),
                            stationDTO,
                            drone.getDroneId()
                    );
                })
                .toList();
    }

    public DroneResponseDTO getDroneById(int id) {
        return droneRepository.findById(id)
                .map(drone -> {
                    Station station = drone.getStation();
                    StationDTO stationDTO = new StationDTO(
                            station.getStationId(),
                            station.getLatitude(),
                            station.getLongitude()
                    );
                    return new DroneResponseDTO(
                            drone.getSerialUuid(),
                            drone.getStatus(),
                            stationDTO,
                            drone.getDroneId()
                    );
                })
                .orElseThrow(() -> new RuntimeException("Drone with id " + id + " not found"));
    }
    public DroneResponseDTO addDrone(DroneRequestDTO requestDTO) {
        // Retrieve all stations
        List<Station> stations = stationRepository.findAll();
        if (stations.isEmpty()) {
            throw new RuntimeException("No stations available to assign the drone.");
        }

        // Find the station with the fewest drones
        Station stationWithFewestDrones = stations.stream()
                .min(Comparator.comparingInt(station -> station.getDroner().size()))
                .orElseThrow();

        // Create a new Drone using the builder pattern
        Drone newDrone = Drone.builder()
                .serialUuid(requestDTO.serialUuid())
                .status(requestDTO.status())
                .station(stationWithFewestDrones)
                .build();

        // Save the Drone to the repository
        Drone savedDrone = droneRepository.save(newDrone);

        // Create a StationDTO for the response
        StationDTO stationDTO = new StationDTO(
                stationWithFewestDrones.getStationId(),
                stationWithFewestDrones.getLatitude(),
                stationWithFewestDrones.getLongitude()
        );

        // Return a DroneResponseDTO with the StationDTO
        return new DroneResponseDTO(
                savedDrone.getSerialUuid(),
                savedDrone.getStatus(),
                stationDTO,
                savedDrone.getDroneId()
        );
    }



    public DroneResponseDTO updateDrone(int id, DroneRequestDTO droneRequestDTO) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone with id " + id + " not found"));

        drone.setSerialUuid(droneRequestDTO.serialUuid());
        drone.setStatus(droneRequestDTO.status());
        drone.setStation(droneRequestDTO.station());

        Drone updatedDrone = droneRepository.save(drone);

        Station station = updatedDrone.getStation();
        StationDTO stationDTO = new StationDTO(
                station.getStationId(),
                station.getLatitude(),
                station.getLongitude()
        );

        return new DroneResponseDTO(
                updatedDrone.getSerialUuid(),
                updatedDrone.getStatus(),
                stationDTO,
                updatedDrone.getDroneId()
        );
    }


    public void deleteDrone(int id) {
        if (!droneRepository.existsById(id)) {
            throw new RuntimeException("Drone with id " + id + " not found");
        }
        droneRepository.deleteById(id);
    }

    public DroneResponseDTO changeDroneStatus(int id, DroneStatus status) {
        Drone drone = droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone with id " + id + " not found"));

        drone.setStatus(status);
        Drone updatedDrone = droneRepository.save(drone);

        Station station = updatedDrone.getStation();
        StationDTO stationDTO = new StationDTO(
                station.getStationId(),
                station.getLatitude(),
                station.getLongitude()
        );

        return new DroneResponseDTO(
                updatedDrone.getSerialUuid(),
                updatedDrone.getStatus(),
                stationDTO,
                updatedDrone.getDroneId()
        );
    }
}
