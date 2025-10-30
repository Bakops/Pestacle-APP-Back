package com.example.pestacle_app.repository;

import com.example.pestacle_app.model.Reservation;
import com.example.pestacle_app.model.Utilisateur;
import com.example.pestacle_app.model.enums.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUtilisateur(Utilisateur utilisateur);

    List<Reservation> findByStatut(StatutReservation statut);

    List<Reservation> findByUtilisateur_IdUtilisateur(Long idUtilisateur);
}
