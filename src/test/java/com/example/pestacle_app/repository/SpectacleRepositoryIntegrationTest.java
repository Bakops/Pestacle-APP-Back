package com.example.pestacle_app.repository;

import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.model.enums.StatutSpectacle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class SpectacleRepositoryIntegrationTest {

    @Autowired
    private SpectacleRepository spectacleRepository;

    @Test
    void findByTitreContainingIgnoreCase_retrouveLeSpectacleSansTenirCompteDeLaCasse() {
        spectacleRepository.save(Spectacle.builder()
                .titre("Festival Jazz")
                .description("Description")
                .dateHeure(LocalDateTime.now().plusDays(1))
                .lieu("Marseille")
                .prixUnitaire(BigDecimal.valueOf(25))
                .placesDisponibles(150)
                .capaciteTotale(150)
                .statut(StatutSpectacle.DISPONIBLE)
                .build());

        List<Spectacle> result = spectacleRepository.findByTitreContainingIgnoreCase("jazz");

        assertEquals(1, result.size());
        assertEquals("Festival Jazz", result.get(0).getTitre());
    }

    @Test
    void findByDateHeureAfterAndStatut_neRetourneQueLesSpectaclesDisponiblesAFutureDate() {
        LocalDateTime now = LocalDateTime.now();

        spectacleRepository.saveAll(List.of(
                Spectacle.builder()
                        .titre("A venir")
                        .description("Description")
                        .dateHeure(now.plusDays(2))
                        .lieu("Paris")
                        .prixUnitaire(BigDecimal.valueOf(20))
                        .placesDisponibles(100)
                        .capaciteTotale(100)
                        .statut(StatutSpectacle.DISPONIBLE)
                        .build(),
                Spectacle.builder()
                        .titre("Passe")
                        .description("Description")
                        .dateHeure(now.minusDays(1))
                        .lieu("Paris")
                        .prixUnitaire(BigDecimal.valueOf(20))
                        .placesDisponibles(100)
                        .capaciteTotale(100)
                        .statut(StatutSpectacle.DISPONIBLE)
                        .build(),
                Spectacle.builder()
                        .titre("Complet")
                        .description("Description")
                        .dateHeure(now.plusDays(3))
                        .lieu("Lyon")
                        .prixUnitaire(BigDecimal.valueOf(20))
                        .placesDisponibles(0)
                        .capaciteTotale(100)
                        .statut(StatutSpectacle.COMPLET)
                        .build()
        ));

        List<Spectacle> result = spectacleRepository.findByDateHeureAfterAndStatut(now, StatutSpectacle.DISPONIBLE);

        assertEquals(1, result.size());
        assertEquals("A venir", result.get(0).getTitre());
    }
}
