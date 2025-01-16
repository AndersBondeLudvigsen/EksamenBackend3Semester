package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.dto.LeveringsResponseDTO;
import org.example.eksamenbackend.dto.StationDTO;
import org.example.eksamenbackend.exception.ResourceNotFoundException;
import org.example.eksamenbackend.exception.DroneAlreadyAssignedException;
import org.example.eksamenbackend.exception.InvalidOperationException;
import org.example.eksamenbackend.model.*;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.LeveringsRepository;
import org.example.eksamenbackend.repository.PizzaRepository;
import org.example.eksamenbackend.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeveringsService {

    private final LeveringsRepository leveringRepository;
    private final PizzaRepository pizzaRepository;
    private final DroneRepository droneRepository;
    private final StationRepository stationRepository;

    public LeveringsService(LeveringsRepository leveringRepository,
                            PizzaRepository pizzaRepository,
                            DroneRepository droneRepository,
                            StationRepository stationRepository) {
        this.leveringRepository = leveringRepository;
        this.pizzaRepository = pizzaRepository;
        this.droneRepository = droneRepository;
        this.stationRepository = stationRepository;
    }


    public List<LeveringsResponseDTO> getAllPendingDeliveries() {
        return leveringRepository.findAllByFaktiskLeveringIsNull().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public LeveringsResponseDTO addDelivery(LeveringsRequestDTO requestDTO) {
        Pizza pizza = pizzaRepository.findById(requestDTO.pizzaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pizza with id " + requestDTO.pizzaId() + " not found"));

        Levering newLevering = Levering.builder()
                .adresse(requestDTO.adresse())
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .pizza(pizza)
                .build();

        Levering savedLevering = leveringRepository.save(newLevering);

        return mapToResponseDTO(savedLevering);
    }

    public List<LeveringsResponseDTO> getQueuedDeliveries() {
        return leveringRepository.findAllByDroneIsNull().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public LeveringsResponseDTO scheduleDelivery(int leveringId, Integer droneId) {
        Levering levering = leveringRepository.findById(leveringId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Levering with id " + leveringId + " not found"));

        if (levering.getDrone() != null) {
            throw new InvalidOperationException("Levering is already scheduled with a drone");
        }

        Drone drone = getAvailableDrone(droneId);

        if (leveringRepository.existsByDroneAndFaktiskLeveringIsNull(drone)) {
            throw new DroneAlreadyAssignedException("Drone is already assigned to another delivery");
        }

        levering.setDrone(drone);
        Levering updatedLevering = leveringRepository.save(levering);

        return mapToResponseDTO(updatedLevering);
    }

    public LeveringsResponseDTO finishDelivery(int leveringId) {
        Levering levering = leveringRepository.findById(leveringId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Levering with id " + leveringId + " not found"));

        if (levering.getDrone() == null) {
            throw new InvalidOperationException("Cannot finish a levering without a drone");
        }

        levering.setFaktiskLevering(LocalDateTime.now());
        Levering finishedLevering = leveringRepository.save(levering);

        return mapToResponseDTO(finishedLevering);
    }

    public LeveringsResponseDTO simulateAddDelivery(LeveringsRequestDTO requestDTO) {
        Pizza pizza = pizzaRepository.findById(requestDTO.pizzaId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Pizza with id " + requestDTO.pizzaId() + " not found"));

        Station station = stationRepository.findAll().stream()
                .filter(s -> s.getDroner().stream().anyMatch(drone -> drone.getStatus() == DroneStatus.OPERATIONAL))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No station with available drones found"));

        Drone drone = station.getDroner().stream()
                .filter(d -> d.getStatus() == DroneStatus.OPERATIONAL)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No available drones at station " + station.getStationId()));

        Levering newLevering = Levering.builder()
                .adresse(requestDTO.adresse())
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .pizza(pizza)
                .drone(drone)
                .build();

        Levering savedLevering = leveringRepository.save(newLevering);

        return mapToResponseDTO(savedLevering);
    }


    private LeveringsResponseDTO mapToResponseDTO(Levering levering) {
        return new LeveringsResponseDTO(
                levering.getLeveringId(),
                levering.getAdresse(),
                levering.getForventetLevering(),
                levering.getFaktiskLevering(),
                getDroneSerialUuid(levering.getDrone()),
                levering.getPizza().getTitel(),
                mapToStationDTO(levering.getDrone())
        );
    }

    private StationDTO mapToStationDTO(Drone drone) {
        if (drone == null || drone.getStation() == null) {
            return null;
        }
        return new StationDTO(
                drone.getStation().getStationId(),
                drone.getStation().getLatitude(),
                drone.getStation().getLongitude()
        );
    }

    private String getDroneSerialUuid(Drone drone) {
        return (drone != null) ? drone.getSerialUuid() : null;
    }


    private Drone getAvailableDrone(Integer droneId) {
        if (droneId != null) {
            return droneRepository.findById(droneId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Drone with id " + droneId + " not found"));
        }

        return droneRepository.findAll().stream()
                .filter(d -> d.getStatus() == DroneStatus.OPERATIONAL)
                .filter(d -> !leveringRepository.existsByDroneAndFaktiskLeveringIsNull(d))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("No available drones found"));
    }
}
