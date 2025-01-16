package org.example.eksamenbackend.config;

import org.example.eksamenbackend.model.Pizza;
import org.example.eksamenbackend.model.Station;
import org.example.eksamenbackend.repository.PizzaRepository;
import org.example.eksamenbackend.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {
    @Autowired
    StationRepository stationRepository;
    @Autowired
    PizzaRepository pizzaRepository;
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
    };
    }


