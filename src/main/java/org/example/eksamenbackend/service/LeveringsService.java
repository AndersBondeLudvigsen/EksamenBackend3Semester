package org.example.eksamenbackend.service;

import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.dto.LeveringsResponseDTO;
import org.example.eksamenbackend.model.Drone;
import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.model.Levering;
import org.example.eksamenbackend.model.Pizza;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.LeveringsRepository;
import org.example.eksamenbackend.repository.PizzaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LeveringsService {

    private final LeveringsRepository leveringRepository;
    private final PizzaRepository pizzaRepository;
    private final DroneRepository droneRepository;

    public LeveringsService(LeveringsRepository leveringRepository, PizzaRepository pizzaRepository, DroneRepository droneRepository) {
        this.leveringRepository = leveringRepository;
        this.pizzaRepository = pizzaRepository;
        this.droneRepository = droneRepository;
    }

    public List<LeveringsResponseDTO> getAllPendingDeliveries() {
        return leveringRepository.findAllByFaktiskLeveringIsNull().stream()
            .map(this::mapToResponseDTO)
            .toList();
    }

    public LeveringsResponseDTO addDelivery(LeveringsRequestDTO requestDTO) {
        Pizza pizza = pizzaRepository.findById(requestDTO.pizzaId())
                .orElseThrow(() -> new RuntimeException("Pizza with id " + requestDTO.pizzaId() + " not found"));

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
            .orElseThrow(() -> new RuntimeException("Levering with id " + leveringId + " not found"));

        if (levering.getDrone() != null) {
            throw new RuntimeException("Levering is already scheduled with a drone");
        }

        Drone drone = (droneId != null) ?
            droneRepository.findById(droneId)
                .orElseThrow(() -> new RuntimeException("Drone with id " + droneId + " not found")) :
            droneRepository.findFirstByStatus(DroneStatus.I_DRIFT)
                .orElseThrow(() -> new RuntimeException("No drones available in 'i drift' status"));

        levering.setDrone(drone);
        Levering updatedLevering = leveringRepository.save(levering);
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
            levering.getPizza().getTitel()
        );
    }
}