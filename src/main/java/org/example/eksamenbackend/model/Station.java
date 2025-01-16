package org.example.eksamenbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stationId;

    private double latitude;

    private double longitude;

    @OneToMany(mappedBy = "station")
    private List<Drone> droner;

    public Station(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}