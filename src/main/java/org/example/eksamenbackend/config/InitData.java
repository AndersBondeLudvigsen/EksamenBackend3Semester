package org.example.eksamenbackend.config;

import org.example.eksamenbackend.model.*;
import org.example.eksamenbackend.repository.DroneRepository;
import org.example.eksamenbackend.repository.LeveringsRepository;
import org.example.eksamenbackend.repository.PizzaRepository;
import org.example.eksamenbackend.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InitData implements CommandLineRunner {
    @Autowired
    StationRepository stationRepository;
    @Autowired
    PizzaRepository pizzaRepository;
    @Autowired
    DroneRepository droneRepository;
    @Autowired
    LeveringsRepository leveringsRepository;
    @Override
    public void run(String... args) throws Exception {
        stationRepository.save(new Station(55.41, 12.34)); // Centrum af København
        stationRepository.save(new Station(55.43, 12.35)); // Tæt på centrum
        stationRepository.save(new Station( 55.40, 12.33)); // Tæt på centrum

        // Initialize Pizzas
        pizzaRepository.save(new Pizza("Margherita", 75));
        pizzaRepository.save(new Pizza( "Pepperoni", 85));
        pizzaRepository.save(new Pizza( "Hawaii", 90));
        pizzaRepository.save(new Pizza( "Vegetar", 80));
        pizzaRepository.save(new Pizza( "Meat Lover", 95));

        droneRepository.save(new Drone("123e4567-e89b-12d3-a456-426614174001", DroneStatus.I_DRIFT, stationRepository.findById(1).orElse(null)));
        droneRepository.save(new Drone("123e4567-e89b-12d3-a456-426614174002", DroneStatus.I_DRIFT, stationRepository.findById(2).orElse(null)));
        droneRepository.save(new Drone("123e4567-e89b-12d3-a456-426614174003", DroneStatus.I_DRIFT, stationRepository.findById(1).orElse(null)));
        droneRepository.save(new Drone("123e4567-e89b-12d3-a456-426614174004", DroneStatus.I_DRIFT, null));
        leveringsRepository.save(Levering.builder()
                .adresse("Test Address 1")
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .faktiskLevering(null)
                .drone(droneRepository.findById(2).orElse(null))
                .pizza(pizzaRepository.findById(1).orElseThrow(() -> new RuntimeException("Pizza not found")))
                .build());

        leveringsRepository.save(Levering.builder()
                .adresse("Test Address 2")
                .forventetLevering(LocalDateTime.now().plusMinutes(45))
                .faktiskLevering(null)
                .drone(null)
                .pizza(pizzaRepository.findById(3).orElseThrow(() -> new RuntimeException("Pizza not found")))
                .build());

        leveringsRepository.save(Levering.builder()
                .adresse("Test Address 3")
                .forventetLevering(LocalDateTime.now().plusMinutes(30))
                .faktiskLevering(null)
                .drone(droneRepository.findById(3).orElse(null))
                .pizza(pizzaRepository.findById(3).orElseThrow(() -> new RuntimeException("Pizza not found")))
                .build());

        leveringsRepository.save(Levering.builder()
                .adresse("Test Address 4")
                .forventetLevering(LocalDateTime.now().plusMinutes(60))
                .faktiskLevering(null)
                .drone(null)
                .pizza(pizzaRepository.findById(4).orElseThrow(() -> new RuntimeException("Pizza not found")))
                .build());

    }
    }


