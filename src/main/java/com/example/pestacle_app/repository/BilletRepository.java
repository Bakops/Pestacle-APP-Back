package com.example.pestacle_app.repository;

import com.example.pestacle_app.model.Billet;
import com.example.pestacle_app.model.Reservation;
import com.example.pestacle_app.model.Spectacle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BilletRepository extends JpaRepository<Billet, Long> {

    List<Billet> findByReservation(Reservation reservation);

    List<Billet> findBySpectacle(Spectacle spectacle);
}
