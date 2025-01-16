package org.example.eksamenbackend.exception;

import org.example.eksamenbackend.dto.LeveringsRequestDTO;
import org.example.eksamenbackend.exception.DroneAlreadyAssignedException;
import org.example.eksamenbackend.exception.InvalidOperationException;
import org.example.eksamenbackend.exception.ResourceNotFoundException;
import org.example.eksamenbackend.model.Drone;
import org.example.eksamenbackend.model.Levering;
import org.example.eksamenbackend.model.Pizza;
import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.LeveringsRepository;
import org.example.eksamenbackend.repository.PizzaRepository;
import org.example.eksamenbackend.repository.StationRepository;
import org.example.eksamenbackend.service.LeveringsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class LeveringsServiceExceptionTest {

    @InjectMocks
    private LeveringsService leveringsService;

    @Mock
    private PizzaRepository pizzaRepository;

    @Mock
    private LeveringsRepository leveringRepository;

    @Mock
    private DroneRepository droneRepository;


    private Pizza pizza;
    private Levering levering;
    private Drone drone;

    @BeforeEach
    void setUp() {
        pizza = Pizza.builder()
                .titel("Margherita")
                .pris(100)
                .build();

        levering = Levering.builder()
                .adresse("Polensgade 2")
                .pizza(pizza)
                .build();

        drone = Drone.builder()
                .serialUuid("DRONE123")
                .status(DroneStatus.OPERATIONAL)
                .build();
    }

    @Test
    void addDelivery_whenPizzaNotFound_shouldThrowResourceNotFoundException() {
        Mockito.when(pizzaRepository.findById(9999)).thenReturn(Optional.empty());
        LeveringsRequestDTO request = new LeveringsRequestDTO("Testvej 1", 9999);

        assertThrows(ResourceNotFoundException.class, () -> {
            leveringsService.addDelivery(request);
        });
    }

    @Test
    void scheduleDelivery_whenDroneAlreadyAssigned_shouldThrowDroneAlreadyAssignedException() {

        Mockito.when(leveringRepository.findById(1)).thenReturn(Optional.of(levering));
        Mockito.when(droneRepository.findById(1)).thenReturn(Optional.of(drone));
        Mockito.when(leveringRepository.existsByDroneAndFaktiskLeveringIsNull(drone)).thenReturn(true);

        assertThrows(DroneAlreadyAssignedException.class, () -> {
            leveringsService.scheduleDelivery(1, 1);
        });
    }

    @Test
    void finishDelivery_whenLeveringHasNoDrone_shouldThrowInvalidOperationException() {
        Mockito.when(leveringRepository.findById(1)).thenReturn(Optional.of(levering));

        assertThrows(InvalidOperationException.class, () -> {
            leveringsService.finishDelivery(1);
        });
    }
}
