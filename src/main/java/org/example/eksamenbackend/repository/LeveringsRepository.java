package org.example.eksamenbackend.repository;

import org.example.eksamenbackend.model.Levering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeveringsRepository extends JpaRepository<Levering,Integer> {
    List<Levering> findAllByFaktiskLeveringIsNull();
    List<Levering> findAllByDroneIsNull();

}
