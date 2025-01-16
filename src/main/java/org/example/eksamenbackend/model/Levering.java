package org.example.eksamenbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class Levering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int leveringId;


    private String adresse;

    private LocalDateTime forventetLevering;

    private LocalDateTime faktiskLevering;

    @ManyToOne
    @JoinColumn(name = "drone_id")
    private Drone drone;

    @ManyToOne
    @JoinColumn(name = "pizza_id")
    private Pizza pizza;



}