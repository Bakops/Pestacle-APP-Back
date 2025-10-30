package com.example.pestacle_app.repository;

import com.example.pestacle_app.model.StatistiqueVente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StatistiqueVenteRepository extends JpaRepository<StatistiqueVente, Long> {
    List<StatistiqueVente> findByDateJourBetween(LocalDate debut, LocalDate fin);

    List<StatistiqueVente> findBySpectacleIdSpectacle(Long spectacleId);

    Optional<StatistiqueVente> findBySpectacleIdSpectacleAndDateJour(Long spectacleId, LocalDate dateJour);
}