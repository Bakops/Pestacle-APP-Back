package com.example.pestacle_app.dto;

import com.example.pestacle_app.model.enums.StatutReservation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationDTO {
    private Long id;
    private LocalDateTime dateReservation;
    private StatutReservation statut;
    private BigDecimal montantTotal;

    @NotNull(message = "Le moyen de paiement est obligatoire")
    private String moyenPaiement;

    @NotNull(message = "L'utilisateur est obligatoire")
    private Long utilisateurId;

    @Size(min = 1, message = "Une réservation doit contenir au moins un billet")
    private List<BilletDTO> billets;
}