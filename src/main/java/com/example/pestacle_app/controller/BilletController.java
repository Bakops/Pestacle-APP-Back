package com.example.pestacle_app.controller;

import com.example.pestacle_app.dto.BilletDTO;
import com.example.pestacle_app.service.BilletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billets")
@RequiredArgsConstructor
@Tag(name = "Billets", description = "API de gestion des billets")
public class BilletController {

    private final BilletService billetService;

    @GetMapping("/reservation/{reservationId}")
    @Operation(summary = "Récupérer tous les billets d'une réservation")
    public ResponseEntity<List<BilletDTO>> getBilletsByReservation(@PathVariable Long reservationId) {
        return ResponseEntity.ok(billetService.getBilletsByReservation(reservationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un billet par son ID")
    public ResponseEntity<BilletDTO> getBillet(@PathVariable Long id) {
        return ResponseEntity.ok(billetService.getBillet(id));
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau billet")
    public ResponseEntity<BilletDTO> createBillet(@Valid @RequestBody BilletDTO billetDTO) {
        return ResponseEntity.ok(billetService.createBillet(billetDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un billet")
    public ResponseEntity<Void> deleteBillet(@PathVariable Long id) {
        billetService.deleteBillet(id);
        return ResponseEntity.ok().build();
    }
}
