package org.example.eksamenbackend.repository;

import org.example.eksamenbackend.model.Pizza;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PizzaRepository extends JpaRepository<Pizza, Integer> {
}
