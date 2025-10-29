package com.example.pestacle_app.controller;

import com.example.pestacle_app.model.Spectacle;
import com.example.pestacle_app.service.SpectacleService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/spectacles")
@CrossOrigin(origins = "*")
public class SpectacleController {

    private final SpectacleService service;

    public SpectacleController(SpectacleService service) {
        this.service = service;
    }

    @GetMapping
    public List<Spectacle> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Spectacle getOne(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Spectacle create(@RequestBody Spectacle spectacle) {
        return service.save(spectacle);
    }

    @PutMapping("/{id}")
    public Spectacle update(@PathVariable Long id, @RequestBody Spectacle spectacle) {
        spectacle.setIdSpectacle(id);
        return service.save(spectacle);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
