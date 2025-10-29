package com.example.pestacle_app.service;
import com.example.pestacle_app.model.Utilisateur;
import com.example.pestacle_app.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UtilisateurService {
    private final UtilisateurRepository repository;

    public UtilisateurService(UtilisateurRepository repository) {
        this.repository = repository;
    }

    public List<Utilisateur> findAll() {
        return repository.findAll();
    }

    public Utilisateur findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public Utilisateur save(Utilisateur utilisateur) {
        return repository.save(utilisateur);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
