package com.example.pestacle_app.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "stats_vente")
public class StatistiqueVente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idStat;

    @ManyToOne
    @JoinColumn(name = "idSpectacle", nullable = false)
    private Spectacle spectacle;

    private LocalDate dateJour;

    private int nbBilletsVendus;

    private BigDecimal montantVentes;
}
