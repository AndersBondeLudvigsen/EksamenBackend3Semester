package org.example.eksamenbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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