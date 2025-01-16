package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.dto.LeveringsResponseDTO;
import org.example.eksamenbackend.dto.StationDTO;
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

    public LeveringsService(LeveringsRepository leveringRepository, PizzaRepository pizzaRepository, DroneRepository droneRepository, StationRepository stationRepository) {
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
        // Find the pizza
        Pizza pizza = pizzaRepository.findById(requestDTO.pizzaId())
                .orElseThrow(() -> new RuntimeException("Pizza with id " + requestDTO.pizzaId() + " not found"));

        // Create a new delivery without a drone
        Levering newLevering = Levering.builder()
                .adresse(requestDTO.adresse())
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .pizza(pizza)
                .build();

        // Save the new delivery
        Levering savedLevering = leveringRepository.save(newLevering);

        return mapToResponseDTO(savedLevering);
    }


    public List<LeveringsResponseDTO> getQueuedDeliveries() {
        return leveringRepository.findAllByDroneIsNull().stream()
            .map(this::mapToResponseDTO)
            .toList();
    }

    public LeveringsResponseDTO scheduleDelivery(int leveringId, Integer droneId) {
        // Find the levering
        Levering levering = leveringRepository.findById(leveringId)
                .orElseThrow(() -> new RuntimeException("Levering with id " + leveringId + " not found"));

        // Check if the levering already has a drone assigned
        if (levering.getDrone() != null) {
            throw new RuntimeException("Levering is already scheduled with a drone");
        }

        // Find the drone (either specified or automatically selected)
        Drone drone = (droneId != null)
                ? droneRepository.findById(droneId)
                .orElseThrow(() -> new RuntimeException("Drone with id " + droneId + " not found"))
                : droneRepository.findAll().stream()
                .filter(d -> d.getStatus() == DroneStatus.I_DRIFT)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available drones found"));

        // Ensure the drone is in "I_DRIFT" status
        if (drone.getStatus() != DroneStatus.I_DRIFT) {
            throw new RuntimeException("Drone is not in 'I_DRIFT' status");
        }

        // Assign the drone to the levering
        levering.setDrone(drone);
        Levering updatedLevering = leveringRepository.save(levering);

        // Map to response DTO and return
        return mapToResponseDTO(updatedLevering);
    }


    public LeveringsResponseDTO finishDelivery(int leveringId) {
        Levering levering = leveringRepository.findById(leveringId)
            .orElseThrow(() -> new RuntimeException("Levering with id " + leveringId + " not found"));

        if (levering.getDrone() == null) {
            throw new RuntimeException("Cannot finish a levering without a drone");
        }

        levering.setFaktiskLevering(LocalDateTime.now());
        Levering finishedLevering = leveringRepository.save(levering);
        return mapToResponseDTO(finishedLevering);
    }

    private LeveringsResponseDTO mapToResponseDTO(Levering levering) {
        return new LeveringsResponseDTO(
                levering.getLeveringId(),
                levering.getAdresse(),
                levering.getForventetLevering(),
                levering.getFaktiskLevering(),
                levering.getDrone() != null ? levering.getDrone().getSerialUuid() : null,
                levering.getPizza().getTitel(),
                levering.getDrone() != null && levering.getDrone().getStation() != null
                        ? new StationDTO(
                        levering.getDrone().getStation().getStationId(),
                        levering.getDrone().getStation().getLatitude(),
                        levering.getDrone().getStation().getLongitude()
                )
                        : null
        );
    }
    public LeveringsResponseDTO simulateAddDelivery(LeveringsRequestDTO requestDTO) {
        Pizza pizza = pizzaRepository.findById(requestDTO.pizzaId())
                .orElseThrow(() -> new RuntimeException("Pizza with id " + requestDTO.pizzaId() + " not found"));

        Station station = stationRepository.findAll().stream()
                .filter(s -> s.getDroner().stream().anyMatch(drone -> drone.getStatus() == DroneStatus.I_DRIFT))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No station with available drones found"));

        Drone drone = station.getDroner().stream()
                .filter(d -> d.getStatus() == DroneStatus.I_DRIFT)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No available drones at station " + station.getStationId()));

        Levering newLevering = Levering.builder()
                .adresse(requestDTO.adresse())
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .pizza(pizza)
                .drone(drone)
                .build();

        Levering savedLevering = leveringRepository.save(newLevering);

        return mapToResponseDTO(savedLevering);
    }


}