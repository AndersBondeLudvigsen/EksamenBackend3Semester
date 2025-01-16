package org.example.eksamenbackend.repository;

import org.example.eksamenbackend.model.Drone;
import org.example.eksamenbackend.model.Levering;
import org.example.eksamenbackend.model.Pizza;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class LeveringsRepositoryTest {

    @Autowired
    LeveringsRepository leveringsRepository;

    @Autowired
    DroneRepository droneRepository;

    @Autowired
    PizzaRepository pizzaRepository;

    private Levering levering1;
    private Levering levering2;
    private Drone drone;
    private Pizza pizza;

    @BeforeEach
    void setUp() {
        pizza = Pizza.builder()
                .titel("Margherita")
                .build();
        pizza = pizzaRepository.save(pizza);

        drone = Drone.builder()
                .serialUuid("DRONE123")
                .build();
        drone = droneRepository.save(drone);

        levering1 = Levering.builder()
                .adresse("Testvej 1")
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .pizza(pizza)
                .build();

        levering2 = Levering.builder()
                .adresse("Testvej 2")
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .drone(drone)
                .pizza(pizza)
                .build();

        levering1 = leveringsRepository.save(levering1);
        levering2 = leveringsRepository.save(levering2);
    }

    @AfterEach
    void tearDown() {
        leveringsRepository.deleteAll();
        droneRepository.deleteAll();
        pizzaRepository.deleteAll();
    }

    @Test
    void findAllByFaktiskLeveringIsNullTest() {
        List<Levering> pendingDeliveries = leveringsRepository.findAllByFaktiskLeveringIsNull();
        assertEquals(2, pendingDeliveries.size());
    }

    @Test
    void findAllByDroneIsNullTest() {
        List<Levering> noDroneDeliveries = leveringsRepository.findAllByDroneIsNull();
        assertEquals(1, noDroneDeliveries.size());
    }

    @Test
    void existsByDroneAndFaktiskLeveringIsNullTest() {
        boolean exists = leveringsRepository.existsByDroneAndFaktiskLeveringIsNull(drone);
        assertTrue(exists);

        levering2.setFaktiskLevering(LocalDateTime.now());
        leveringsRepository.save(levering2);

        exists = leveringsRepository.existsByDroneAndFaktiskLeveringIsNull(drone);
        assertFalse(exists);
    }

}
