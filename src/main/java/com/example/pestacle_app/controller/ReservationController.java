package com.example.pestacle_app.controller;
import com.example.pestacle_app.dto.ReservationDTO;
import com.example.pestacle_app.service.ReservationService;
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
@RequestMapping("/api/reservations")
@Tag(name = "Réservations", description = "API de gestion des réservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @Operation(summary = "Obtenir toutes les réservations", description = "Retourne une liste paginée de toutes les réservations")
    public ResponseEntity<Page<ReservationDTO>> getAllReservations(
            @Parameter(description = "Paramètres de pagination") Pageable pageable) {
        return ResponseEntity.ok(reservationService.getAllReservations(pageable));
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    @Operation(summary = "Obtenir les réservations d'un utilisateur", description = "Retourne la liste des réservations d'un utilisateur spécifique")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUtilisateur(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long utilisateurId) {
        return ResponseEntity.ok(reservationService.getReservationsByUtilisateur(utilisateurId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une réservation par son ID", description = "Retourne les détails d'une réservation spécifique")
    public ResponseEntity<ReservationDTO> getReservation(
            @Parameter(description = "ID de la réservation") @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation", description = "Crée une nouvelle réservation avec les billets associés")
    public ResponseEntity<ReservationDTO> createReservation(
            @Parameter(description = "Détails de la réservation") @Valid @RequestBody ReservationDTO reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une réservation", description = "Met à jour les détails d'une réservation existante")
    public ResponseEntity<ReservationDTO> updateReservation(
            @Parameter(description = "ID de la réservation") @PathVariable Long id,
            @Parameter(description = "Nouvelles données de la réservation") @Valid @RequestBody ReservationDTO reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Annuler une réservation", description = "Annule une réservation et libère les places associées")
    public ResponseEntity<Void> annulerReservation(
            @Parameter(description = "ID de la réservation") @PathVariable Long id) {
        reservationService.annulerReservation(id);
        return ResponseEntity.ok().build();
    }
}
