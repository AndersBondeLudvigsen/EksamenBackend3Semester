package org.example.eksamenbackend.dto;

import org.example.eksamenbackend.model.DroneStatus;


public record DroneResponseDTO(String serialUuid, DroneStatus status, StationDTO station, int droneId) {}
