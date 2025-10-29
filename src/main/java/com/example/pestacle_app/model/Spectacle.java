package com.example.pestacle_app.model;

import com.example.pestacle_app.model.enums.StatutSpectacle;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Spectacle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSpectacle;

    @Column(nullable = false, length = 255)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    private String lieu;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    private String imageUrl;

    private int placesDisponibles;

    @Enumerated(EnumType.STRING)
    private StatutSpectacle statut = StatutSpectacle.ACTIF;

    @OneToMany(mappedBy = "spectacle", cascade = CascadeType.ALL)
    private List<Billet> billets;
}
