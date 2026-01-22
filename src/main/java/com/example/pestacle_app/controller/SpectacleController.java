package com.example.pestacle_app.controller;

import com.example.pestacle_app.dto.SpectacleDTO;
import com.example.pestacle_app.service.SpectacleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spectacles")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Spectacles", description = "API de gestion des spectacles")
@RequiredArgsConstructor
public class SpectacleController {

    private final SpectacleService service;

    @GetMapping
    @Operation(summary = "Obtenir tous les spectacles", description = "Retourne une liste paginée de tous les spectacles")
    public ResponseEntity<Page<SpectacleDTO>> getAllSpectacles(
            @Parameter(description = "Paramètres de pagination") Pageable pageable) {
        return ResponseEntity.ok(service.getAllSpectacles(pageable));
    }

    @GetMapping("/a-venir")
    @Operation(summary = "Obtenir les spectacles à venir", description = "Retourne la liste des spectacles disponibles avec une date future")
    public ResponseEntity<List<SpectacleDTO>> getSpectaclesAVenir() {
        return ResponseEntity.ok(service.getSpectaclesAVenir());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir un spectacle par son ID", description = "Retourne les détails d'un spectacle spécifique")
    public ResponseEntity<SpectacleDTO> getSpectacle(
            @Parameter(description = "ID du spectacle") @PathVariable Long id) {
        return ResponseEntity.ok(service.getSpectacleById(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau spectacle", description = "Crée un nouveau spectacle (réservé aux administrateurs)")
    public ResponseEntity<SpectacleDTO> createSpectacle(
            @Parameter(description = "Détails du spectacle à créer") @Valid @RequestBody SpectacleDTO spectacle) {
        return ResponseEntity.ok(service.createSpectacle(spectacle));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un spectacle", description = "Met à jour les détails d'un spectacle existant (réservé aux administrateurs)")
    public ResponseEntity<SpectacleDTO> updateSpectacle(
            @Parameter(description = "ID du spectacle") @PathVariable Long id,
            @Parameter(description = "Nouvelles données du spectacle") @Valid @RequestBody SpectacleDTO spectacle) {
        return ResponseEntity.ok(service.updateSpectacle(id, spectacle));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un spectacle", description = "Supprime un spectacle (réservé aux administrateurs)")
    public ResponseEntity<Void> deleteSpectacle(
            @Parameter(description = "ID du spectacle") @PathVariable Long id) {
        service.deleteSpectacle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reserver/{nombrePlaces}")
    @Operation(summary = "Réserver des places", description = "Tente de réserver un nombre spécifique de places pour un spectacle")
    public ResponseEntity<Boolean> reserverPlaces(
            @Parameter(description = "ID du spectacle") @PathVariable Long id,
            @Parameter(description = "Nombre de places à réserver") @PathVariable int nombrePlaces) {
        boolean success = service.reserverPlaces(id, nombrePlaces);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/{id}/annuler/{nombrePlaces}")
    @Operation(summary = "Annuler une réservation", description = "Annule une réservation et remet les places en disponibilité")
    public ResponseEntity<Void> annulerReservation(
            @Parameter(description = "ID du spectacle") @PathVariable Long id,
            @Parameter(description = "Nombre de places à annuler") @PathVariable int nombrePlaces) {
        service.annulerReservation(id, nombrePlaces);
        return ResponseEntity.ok().build();
    }
}
