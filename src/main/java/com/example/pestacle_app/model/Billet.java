package com.example.pestacle_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "billet")
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idBillet")
    private Long idBillet;

    @ManyToOne
    @JoinColumn(name = "idReservation", nullable = false)
    private com.example.pestacle_app.model.Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "idSpectacle", nullable = false)
    private com.example.pestacle_app.model.Spectacle spectacle;

    private int quantite = 1;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Transient
    public BigDecimal getPrixLigne() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
}
