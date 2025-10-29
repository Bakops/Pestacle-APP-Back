package com.example.pestacle_app.service;
import com.example.pestacle_app.model.Reservation;
import com.example.pestacle_app.repository.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public List<Reservation> findAll() {
        return repository.findAll();
    }

    public Reservation findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation introuvable"));
    }

    public Reservation save(Reservation reservation) {
        return repository.save(reservation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
