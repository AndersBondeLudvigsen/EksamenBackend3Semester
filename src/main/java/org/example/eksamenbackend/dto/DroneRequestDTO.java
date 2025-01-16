package org.example.eksamenbackend.dto;

import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.model.Station;

public record DroneRequestDTO(String serialUuid, DroneStatus status, Station station) {
}
