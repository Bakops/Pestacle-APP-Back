package com.example.pestacle_app.service;

import com.example.pestacle_app.dto.BilletDTO;
import com.example.pestacle_app.model.Billet;
import com.example.pestacle_app.model.Reservation;
import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.repository.BilletRepository;
import com.example.pestacle_app.repository.ReservationRepository;
import com.example.pestacle_app.repository.SpectacleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BilletService {

    private final BilletRepository billetRepository;
    private final ReservationRepository reservationRepository;
    private final SpectacleRepository spectacleRepository;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<BilletDTO> getBilletsByReservation(Long reservationId) {
        return billetRepository.findByReservation_IdReservation(reservationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BilletDTO createBillet(BilletDTO billetDTO) {
        Reservation reservation = reservationRepository.findById(billetDTO.getReservationId())
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée"));

        Spectacle spectacle = spectacleRepository.findById(billetDTO.getSpectacleId())
                .orElseThrow(() -> new EntityNotFoundException("Spectacle non trouvé"));

        if (spectacle.getPlacesDisponibles() < billetDTO.getQuantite()) {
            throw new IllegalStateException("Pas assez de places disponibles");
        }

        Billet billet = convertToEntity(billetDTO);
        billet.setReservation(reservation);
        billet.setSpectacle(spectacle);
        billet.setPrixUnitaire(spectacle.getPrixUnitaire());

        spectacle.setPlacesDisponibles(spectacle.getPlacesDisponibles() - billetDTO.getQuantite());
        spectacleRepository.save(spectacle);

        return convertToDTO(billetRepository.save(billet));
    }

    @Transactional
    public void deleteBillet(Long id) {
        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billet non trouvé"));

        Spectacle spectacle = billet.getSpectacle();
        spectacle.setPlacesDisponibles(spectacle.getPlacesDisponibles() + billet.getQuantite());
        spectacleRepository.save(spectacle);

        billetRepository.delete(billet);
    }

    @Transactional(readOnly = true)
    public BilletDTO getBillet(Long id) {
        return billetRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Billet non trouvé"));
    }

    @Transactional
    public void annulerBillet(Long id) {
        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billet non trouvé"));

        // Remettre les places en disponibilité
        Spectacle spectacle = billet.getSpectacle();
        spectacle.setPlacesDisponibles(spectacle.getPlacesDisponibles() + billet.getQuantite());
        spectacleRepository.save(spectacle);

        // Supprimer le billet
        billetRepository.delete(billet);
    }

    private BilletDTO convertToDTO(Billet billet) {
        return modelMapper.map(billet, BilletDTO.class);
    }

    private Billet convertToEntity(BilletDTO billetDTO) {
        return modelMapper.map(billetDTO, Billet.class);
    }
}
