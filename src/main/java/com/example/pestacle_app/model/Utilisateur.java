package com.example.pestacle_app.model;

import com.example.pestacle_app.model.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUtilisateur;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 100)
    private String prenom;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String motDePasse;

    private String telephone;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CLIENT;

    private LocalDateTime dateCreation = LocalDateTime.now();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<Reservation> reservations;
}
