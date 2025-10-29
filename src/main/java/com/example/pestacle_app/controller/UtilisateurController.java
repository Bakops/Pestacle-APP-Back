package com.example.pestacle_app.controller;
import com.example.pestacle_app.model.Utilisateur;
import com.example.pestacle_app.service.UtilisateurService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService service;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.service = utilisateurService;
    }

    @GetMapping
    public List<Utilisateur> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Utilisateur getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Utilisateur create(@RequestBody Utilisateur utilisateur) {
        return service.save(utilisateur);
    }

    @PutMapping("/{id}")
    public Utilisateur update(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        utilisateur.setIdUtilisateur(id);
        return service.save(utilisateur);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
