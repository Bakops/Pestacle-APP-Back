package com.example.pestacle_app.repository;

import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.model.enums.StatutSpectacle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpectacleRepository extends JpaRepository<Spectacle, Long> {
    List<Spectacle> findByTitreContainingIgnoreCase(String titre);
    List<Spectacle> findByStatut(StatutSpectacle statut);
    List<Spectacle> findByDateHeureAfter(LocalDateTime date);
}
