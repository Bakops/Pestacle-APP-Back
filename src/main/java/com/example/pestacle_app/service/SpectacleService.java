package com.example.pestacle_app.service;

import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.repository.SpectacleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpectacleService {

    private final SpectacleRepository repository;

    public SpectacleService(SpectacleRepository repository) {
        this.repository = repository;
    }

    public List<Spectacle> findAll() {
        return repository.findAll();
    }

    public Spectacle findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Spectacle introuvable"));
    }

    public Spectacle save(Spectacle spectacle) {
        return repository.save(spectacle);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
