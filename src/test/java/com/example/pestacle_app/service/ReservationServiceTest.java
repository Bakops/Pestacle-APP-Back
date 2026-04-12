package com.example.pestacle_app.service;

import com.example.pestacle_app.dto.BilletDTO;
import com.example.pestacle_app.dto.ReservationDTO;
import com.example.pestacle_app.model.Billet;
import com.example.pestacle_app.model.Reservation;
import com.example.pestacle_app.model.Utilisateur;
import com.example.pestacle_app.model.enums.StatutReservation;
import com.example.pestacle_app.repository.ReservationRepository;
import com.example.pestacle_app.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private BilletService billetService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_associeLUtilisateurEtInitialiseLeStatut() {
        ReservationDTO input = new ReservationDTO();
        input.setUtilisateurId(5L);
        input.setMoyenPaiement("CB");
        input.setBillets(List.of(new BilletDTO()));

        Utilisateur utilisateur = Utilisateur.builder().idUtilisateur(5L).build();
        Reservation mapped = Reservation.builder().build();
        Reservation saved = Reservation.builder().idReservation(8L).build();
        ReservationDTO output = new ReservationDTO();
        output.setId(8L);

        when(utilisateurRepository.findById(5L)).thenReturn(Optional.of(utilisateur));
        when(modelMapper.map(input, Reservation.class)).thenReturn(mapped);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(saved);
        when(modelMapper.map(saved, ReservationDTO.class)).thenReturn(output);

        ReservationDTO result = reservationService.createReservation(input);

        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());
        assertEquals(utilisateur, captor.getValue().getUtilisateur());
        assertEquals(StatutReservation.EN_ATTENTE, captor.getValue().getStatut());
        assertEquals(8L, input.getBillets().get(0).getReservationId());
        assertEquals(8L, result.getId());
    }

    @Test
    void annulerReservation_passeLaReservationEnAnnuleeEtAnnuleLesBillets() {
        Billet billet = Billet.builder().idBillet(3L).build();
        Reservation reservation = Reservation.builder()
                .idReservation(9L)
                .statut(StatutReservation.CONFIRMEE)
                .billets(List.of(billet))
                .build();

        when(reservationRepository.findById(9L)).thenReturn(Optional.of(reservation));

        reservationService.annulerReservation(9L);

        assertEquals(StatutReservation.ANNULEE, reservation.getStatut());
        verify(billetService).annulerBillet(3L);
        verify(reservationRepository).save(reservation);
    }
}
