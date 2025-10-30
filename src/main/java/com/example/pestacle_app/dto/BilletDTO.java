package com.example.pestacle_app.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BilletDTO {
    private Long id;

    @Min(value = 1, message = "La quantité doit être au moins 1")
    private int quantite;

    @NotNull(message = "Le prix unitaire est obligatoire")
    private BigDecimal prixUnitaire;

    private BigDecimal prixLigne;

    @NotNull(message = "L'ID du spectacle est obligatoire")
    private Long spectacleId;

    @NotNull(message = "L'ID de la réservation est obligatoire")
    private Long reservationId;
}