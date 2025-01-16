package org.example.eksamenbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int droneId;

    @Column(unique = true, nullable = false)
    private String serialUuid;

    @Enumerated(EnumType.STRING) // Use EnumType.STRING to store the enum name in the database
    private DroneStatus status;
    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @OneToMany(mappedBy = "drone")
    private List<Levering> leveringer;

    public Drone(String serialUuid, DroneStatus status, Station station) {
        this.serialUuid = serialUuid;
        this.status = status;
        this.station = station;
    }
}