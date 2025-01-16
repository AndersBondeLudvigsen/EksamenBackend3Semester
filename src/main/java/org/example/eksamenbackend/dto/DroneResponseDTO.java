package org.example.eksamenbackend.dto;

import org.example.eksamenbackend.model.DroneStatus;
import org.example.eksamenbackend.model.Station;


public record DroneResponseDTO(String serialUuid, DroneStatus status, StationDTO station, int droneId) {}
