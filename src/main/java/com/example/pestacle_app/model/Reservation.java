package com.example.pestacle_app.model;

import com.example.pestacle_app.model.enums.StatutReservation;
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
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur", nullable = false)
    private com.example.pestacle_app.model.Utilisateur utilisateur;

    private LocalDateTime dateReservation = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    private BigDecimal montantTotal = BigDecimal.ZERO;

    private String moyenPaiement;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    private List<Billet> billets;
}
