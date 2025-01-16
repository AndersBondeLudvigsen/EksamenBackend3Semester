package org.example.eksamenbackend.repository;

import org.example.eksamenbackend.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {
}
