package com.example.pestacle_app.dto;

import com.example.pestacle_app.model.enums.StatutSpectacle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SpectacleDTO {
    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @Future(message = "La date doit être dans le futur")
    private LocalDateTime dateHeure;

    @NotBlank(message = "Le lieu est obligatoire")
    private String lieu;

    @DecimalMin(value = "0.0", message = "Le prix doit être positif")
    private BigDecimal prixUnitaire;

    private String imageUrl;

    @Min(value = 0, message = "Le nombre de places ne peut pas être négatif")
    private int placesDisponibles;

    private StatutSpectacle statut;
}