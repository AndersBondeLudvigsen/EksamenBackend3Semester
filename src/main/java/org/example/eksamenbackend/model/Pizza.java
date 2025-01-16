package org.example.eksamenbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Pizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pizzaId;

    private String titel;

    private int pris;

    public Pizza(String titel, int pris) {
        this.titel = titel;
        this.pris = pris;
    }
}