package com.example.pestacle_app.service;

import com.example.pestacle_app.dto.SpectacleDTO;
import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.model.enums.StatutSpectacle;
import com.example.pestacle_app.repository.SpectacleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class SpectacleServiceUnitTest {

    @Mock
    private SpectacleRepository spectacleRepository;

    @InjectMocks
    private SpectacleService spectacleService;

    @BeforeEach
    void setUp() {
        spectacleService = new SpectacleService(spectacleRepository, new ModelMapper());
    }

    @Test
    void reserverPlacesShouldReturnTrueAndUpdateStatusToCompletWhenNoSeatsLeft() {
        Spectacle spectacle = buildSpectacle(1L, 2, 100, StatutSpectacle.DISPONIBLE);
        when(spectacleRepository.findById(1L)).thenReturn(Optional.of(spectacle));

        boolean result = spectacleService.reserverPlaces(1L, 2);

        assertTrue(result);
        assertEquals(0, spectacle.getPlacesDisponibles());
        assertEquals(StatutSpectacle.COMPLET, spectacle.getStatut());
        verify(spectacleRepository).save(spectacle);
    }

    @Test
    void reserverPlacesShouldReturnFalseWhenNotEnoughSeats() {
        Spectacle spectacle = buildSpectacle(1L, 1, 100, StatutSpectacle.DISPONIBLE);
        when(spectacleRepository.findById(1L)).thenReturn(Optional.of(spectacle));

        boolean result = spectacleService.reserverPlaces(1L, 3);

        assertFalse(result);
        verify(spectacleRepository, never()).save(any(Spectacle.class));
    }

    @Test
    void deleteSpectacleShouldThrowWhenReservationsExist() {
        Spectacle spectacle = buildSpectacle(1L, 90, 100, StatutSpectacle.DISPONIBLE);
        when(spectacleRepository.findById(1L)).thenReturn(Optional.of(spectacle));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> spectacleService.deleteSpectacle(1L));

        assertEquals("Impossible de supprimer un spectacle avec des réservations", exception.getMessage());
        verify(spectacleRepository, never()).delete(any(Spectacle.class));
    }

    @Test
    void getSpectacleByIdShouldThrowWhenNotFound() {
        when(spectacleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> spectacleService.getSpectacleById(999L));
    }

    @Test
    void createSpectacleShouldForceStatutDisponible() {
        SpectacleDTO dto = new SpectacleDTO();
        dto.setTitre("Concert test");
        dto.setDescription("Description");
        dto.setDateHeure(LocalDateTime.now().plusDays(1));
        dto.setLieu("Paris");
        dto.setPrixUnitaire(BigDecimal.valueOf(25));
        dto.setPlacesDisponibles(100);
        dto.setStatut(StatutSpectacle.ANNULE);

        Spectacle saved = new Spectacle();
        saved.setIdSpectacle(1L);
        saved.setTitre(dto.getTitre());
        saved.setDescription(dto.getDescription());
        saved.setDateHeure(dto.getDateHeure());
        saved.setLieu(dto.getLieu());
        saved.setPrixUnitaire(dto.getPrixUnitaire());
        saved.setPlacesDisponibles(dto.getPlacesDisponibles());
        saved.setStatut(StatutSpectacle.DISPONIBLE);

        when(spectacleRepository.save(any(Spectacle.class))).thenReturn(saved);

        SpectacleDTO result = spectacleService.createSpectacle(dto);

        assertEquals(StatutSpectacle.DISPONIBLE, result.getStatut());
    }

    private Spectacle buildSpectacle(Long id, int placesDisponibles, int capaciteTotale, StatutSpectacle statut) {
        Spectacle spectacle = new Spectacle();
        spectacle.setIdSpectacle(id);
        spectacle.setTitre("Spectacle test");
        spectacle.setDescription("Description test");
        spectacle.setDateHeure(LocalDateTime.now().plusDays(10));
        spectacle.setLieu("Lyon");
        spectacle.setPrixUnitaire(BigDecimal.valueOf(19.99));
        spectacle.setPlacesDisponibles(placesDisponibles);
        spectacle.setCapaciteTotale(capaciteTotale);
        spectacle.setStatut(statut);
        return spectacle;
    }
}
