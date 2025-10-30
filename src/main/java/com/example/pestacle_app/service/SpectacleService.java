package com.example.pestacle_app.service;

import com.example.pestacle_app.dto.SpectacleDTO;
import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.model.enums.StatutSpectacle;
import com.example.pestacle_app.repository.SpectacleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpectacleService {

    private final SpectacleRepository spectacleRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<SpectacleDTO> getAllSpectacles(Pageable pageable) {
        return spectacleRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<SpectacleDTO> getSpectaclesAVenir() {
        return spectacleRepository.findByDateHeureAfterAndStatut(
                LocalDateTime.now(),
                StatutSpectacle.DISPONIBLE)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SpectacleDTO getSpectacleById(Long id) {
        return spectacleRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Spectacle non trouvé"));
    }

    @Transactional
    public SpectacleDTO createSpectacle(SpectacleDTO spectacleDTO) {
        Spectacle spectacle = convertToEntity(spectacleDTO);
        spectacle.setStatut(StatutSpectacle.DISPONIBLE);
        return convertToDTO(spectacleRepository.save(spectacle));
    }

    @Transactional
    public SpectacleDTO updateSpectacle(Long id, SpectacleDTO spectacleDTO) {
        Spectacle existingSpectacle = spectacleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spectacle non trouvé"));

        modelMapper.map(spectacleDTO, existingSpectacle);
        existingSpectacle.setIdSpectacle(id);

        return convertToDTO(spectacleRepository.save(existingSpectacle));
    }

    @Transactional
    public void deleteSpectacle(Long id) {
        Spectacle spectacle = spectacleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spectacle non trouvé"));

        if (spectacle.getPlacesDisponibles() < spectacle.getCapaciteTotale()) {
            throw new IllegalStateException("Impossible de supprimer un spectacle avec des réservations");
        }

        spectacleRepository.delete(spectacle);
    }

    @Transactional
    public boolean reserverPlaces(Long id, int nombrePlaces) {
        Spectacle spectacle = spectacleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spectacle non trouvé"));

        if (spectacle.getStatut() != StatutSpectacle.DISPONIBLE) {
            return false;
        }

        if (spectacle.getPlacesDisponibles() < nombrePlaces) {
            return false;
        }

        spectacle.setPlacesDisponibles(spectacle.getPlacesDisponibles() - nombrePlaces);

        if (spectacle.getPlacesDisponibles() == 0) {
            spectacle.setStatut(StatutSpectacle.COMPLET);
        }

        spectacleRepository.save(spectacle);
        return true;
    }

    @Transactional
    public void annulerReservation(Long id, int nombrePlaces) {
        Spectacle spectacle = spectacleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Spectacle non trouvé"));

        if (spectacle.getStatut() == StatutSpectacle.ANNULE) {
            throw new IllegalStateException("Impossible d'annuler une réservation pour un spectacle annulé");
        }

        spectacle.setPlacesDisponibles(spectacle.getPlacesDisponibles() + nombrePlaces);

        if (spectacle.getStatut() == StatutSpectacle.COMPLET && spectacle.getPlacesDisponibles() > 0) {
            spectacle.setStatut(StatutSpectacle.DISPONIBLE);
        }

        spectacleRepository.save(spectacle);
    }

    private SpectacleDTO convertToDTO(Spectacle spectacle) {
        return modelMapper.map(spectacle, SpectacleDTO.class);
    }

    private Spectacle convertToEntity(SpectacleDTO spectacleDTO) {
        return modelMapper.map(spectacleDTO, Spectacle.class);
    }
}
