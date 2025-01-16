package org.example.eksamenbackend.dto;

import java.time.LocalDateTime;

public record LeveringsResponseDTO(
    int leveringId,
    String adresse,
    LocalDateTime forventetLevering,
    LocalDateTime faktiskLevering,
    String droneSerialUuid,
    String pizzaName,
    StationDTO station

) {}
