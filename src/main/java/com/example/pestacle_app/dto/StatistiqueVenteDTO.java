package com.example.pestacle_app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StatistiqueVenteDTO {
    private Long id;
    private LocalDate dateJour;
    private int nbBilletsVendus;
    private BigDecimal montantVentes;
    private Long spectacleId;
}