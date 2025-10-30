package com.example.pestacle_app.service;
import com.example.pestacle_app.dto.ReservationDTO;
import com.example.pestacle_app.model.Reservation;
import com.example.pestacle_app.model.Utilisateur;
import com.example.pestacle_app.model.enums.StatutReservation;
import com.example.pestacle_app.repository.ReservationRepository;
import com.example.pestacle_app.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final BilletService billetService;
    private final ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public Page<ReservationDTO> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ReservationDTO> getReservationsByUtilisateur(Long utilisateurId) {
        return reservationRepository.findByUtilisateur_IdUtilisateur(utilisateurId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationDTO getReservationById(Long id) {
        return reservationRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée"));
    }

    @Transactional
    public ReservationDTO createReservation(ReservationDTO reservationDTO) {
        Utilisateur utilisateur = utilisateurRepository.findById(reservationDTO.getUtilisateurId())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Reservation reservation = convertToEntity(reservationDTO);
        reservation.setUtilisateur(utilisateur);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        reservation = reservationRepository.save(reservation);

        if (reservationDTO.getBillets() != null) {
            Reservation finalReservation = reservation;
            reservationDTO.getBillets().forEach(billetDTO -> {
                billetDTO.setReservationId(finalReservation.getIdReservation());
                billetService.createBillet(billetDTO);
            });
        }

        return convertToDTO(reservation);
    }

    @Transactional
    public ReservationDTO updateReservation(Long id, ReservationDTO reservationDTO) {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée"));

        if (existingReservation.getStatut() == StatutReservation.ANNULEE) {
            throw new IllegalStateException("Impossible de modifier une réservation annulée");
        }

        modelMapper.map(reservationDTO, existingReservation);
        existingReservation.setIdReservation(id);

        return convertToDTO(reservationRepository.save(existingReservation));
    }

    @Transactional
    public void annulerReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée"));

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new IllegalStateException("La réservation est déjà annulée");
        }

        reservation.setStatut(StatutReservation.ANNULEE);

        // Annuler tous les billets associés
        reservation.getBillets().forEach(billet -> billetService.annulerBillet(billet.getIdBillet()));

        reservationRepository.save(reservation);
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        return modelMapper.map(reservation, ReservationDTO.class);
    }

    private Reservation convertToEntity(ReservationDTO reservationDTO) {
        return modelMapper.map(reservationDTO, Reservation.class);
    }
}
