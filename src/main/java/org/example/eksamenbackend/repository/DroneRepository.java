package org.example.eksamenbackend.repository;

import org.example.eksamenbackend.model.Drone;
import org.example.eksamenbackend.model.DroneStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone, Integer> {
    Optional<Drone> findFirstByStatus(DroneStatus status);

}
