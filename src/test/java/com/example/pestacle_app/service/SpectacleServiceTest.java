package com.example.pestacle_app.service;

import com.example.pestacle_app.dto.SpectacleDTO;
import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.model.enums.StatutSpectacle;
import com.example.pestacle_app.repository.SpectacleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpectacleServiceTest {

    @Mock
    private SpectacleRepository spectacleRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private SpectacleService spectacleService;

    @Test
    void createSpectacle_definitLeStatutDisponibleAvantSauvegarde() {
        SpectacleDTO input = new SpectacleDTO();
        input.setTitre("Concert");
        input.setDescription("Description");
        input.setDateHeure(LocalDateTime.now().plusDays(1));
        input.setLieu("Paris");
        input.setPrixUnitaire(BigDecimal.TEN);
        input.setPlacesDisponibles(50);

        Spectacle mapped = Spectacle.builder().titre("Concert").build();
        Spectacle saved = Spectacle.builder()
                .idSpectacle(1L)
                .titre("Concert")
                .statut(StatutSpectacle.DISPONIBLE)
                .build();
        SpectacleDTO output = new SpectacleDTO();
        output.setId(1L);
        output.setTitre("Concert");

        when(modelMapper.map(input, Spectacle.class)).thenReturn(mapped);
        when(spectacleRepository.save(any(Spectacle.class))).thenReturn(saved);
        when(modelMapper.map(saved, SpectacleDTO.class)).thenReturn(output);

        SpectacleDTO result = spectacleService.createSpectacle(input);

        ArgumentCaptor<Spectacle> captor = ArgumentCaptor.forClass(Spectacle.class);
        verify(spectacleRepository).save(captor.capture());
        assertEquals(StatutSpectacle.DISPONIBLE, captor.getValue().getStatut());
        assertEquals(1L, result.getId());
    }

    @Test
    void reserverPlaces_retourneFalseQuandLeSpectacleEstComplet() {
        Spectacle spectacle = Spectacle.builder()
                .idSpectacle(1L)
                .placesDisponibles(0)
                .statut(StatutSpectacle.COMPLET)
                .build();

        when(spectacleRepository.findById(1L)).thenReturn(Optional.of(spectacle));

        boolean result = spectacleService.reserverPlaces(1L, 1);

        assertFalse(result);
        verify(spectacleRepository, never()).save(any(Spectacle.class));
    }

    @Test
    void annulerReservation_redonneDesPlacesEtRendLeSpectacleDisponible() {
        Spectacle spectacle = Spectacle.builder()
                .idSpectacle(2L)
                .placesDisponibles(0)
                .statut(StatutSpectacle.COMPLET)
                .build();

        when(spectacleRepository.findById(2L)).thenReturn(Optional.of(spectacle));

        spectacleService.annulerReservation(2L, 2);

        assertEquals(2, spectacle.getPlacesDisponibles());
        assertEquals(StatutSpectacle.DISPONIBLE, spectacle.getStatut());
        verify(spectacleRepository).save(spectacle);
        assertTrue(spectacle.getPlacesDisponibles() > 0);
    }
}
