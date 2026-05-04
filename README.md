# Tests du projet Pestacle

Ce document explique les tests actuellement présents dans le projet, leur objectif et ce qu'ils vérifient concrètement.

## Lancer les tests

Sous Windows :

```powershell
.\mvnw.cmd test
```

## Tests unitaires

Les tests unitaires vérifient une règle métier isolée en mockant les dépendances.

### `SpectacleServiceTest`

Fichier : `src/test/java/com/example/pestacle_app/service/SpectacleServiceTest.java`

Tests présents :

1. `createSpectacle_definitLeStatutDisponibleAvantSauvegarde`
   Vérifie qu'un spectacle créé par le service reçoit automatiquement le statut `DISPONIBLE` avant l'appel au repository.
   Justification : cette règle métier appartient au service et ne doit pas dépendre d'une saisie côté client.

2. `reserverPlaces_retourneFalseQuandLeSpectacleEstComplet`
   Vérifie que la réservation échoue si le spectacle est déjà `COMPLET`, et qu'aucune sauvegarde n'est faite.
   Justification : cela protège le stock de places et évite une modification incohérente de l'état du spectacle.

3. `annulerReservation_redonneDesPlacesEtRendLeSpectacleDisponible`
   Vérifie qu'une annulation remet des places disponibles et remet le statut à `DISPONIBLE` quand le spectacle n'est plus complet.
   Justification : c'est la règle inverse de la réservation, donc elle doit être testée explicitement.

### `ReservationServiceTest`

Fichier : `src/test/java/com/example/pestacle_app/service/ReservationServiceTest.java`

Tests présents :

1. `createReservation_associeLUtilisateurEtInitialiseLeStatut`
   Vérifie qu'une réservation créée est liée au bon utilisateur, passe au statut `EN_ATTENTE` et transmet l'identifiant de réservation aux billets.
   Justification : ce test contrôle le minimum nécessaire lors de la création d'une réservation, sans tester trop de branches en même temps.

2. `annulerReservation_passeLaReservationEnAnnuleeEtAnnuleLesBillets`
   Vérifie qu'une réservation confirmée passe au statut `ANNULEE`, que ses billets sont annulés et qu'elle est sauvegardée.
   Justification : l'annulation a des effets sur plusieurs objets, donc ce comportement central doit être couvert.

## Tests d'intégration

Les tests d'intégration vérifient qu'une partie réelle de Spring fonctionne correctement avec sa configuration.

### `SpectacleControllerIntegrationTest`

Fichier : `src/test/java/com/example/pestacle_app/controller/SpectacleControllerIntegrationTest.java`

Type : test Web MVC avec `MockMvc`

Tests présents :

1. `getSpectacle_retourneLeSpectacleDemande`
   Vérifie que l'endpoint `GET /api/spectacles/{id}` retourne une réponse HTTP 200 avec les bonnes données JSON.
   Justification : ce test prouve que le contrôleur expose correctement la ressource côté API.

2. `createSpectacle_refuseUnPayloadInvalide`
   Vérifie que l'endpoint `POST /api/spectacles` retourne HTTP 400 si le champ `titre` est absent.
   Justification : ce test couvre la validation d'entrée, essentielle pour éviter des données invalides dès la couche HTTP.

### `SpectacleRepositoryIntegrationTest`

Fichier : `src/test/java/com/example/pestacle_app/repository/SpectacleRepositoryIntegrationTest.java`

Type : test JPA avec base H2 en mémoire

Tests présents :

1. `findByTitreContainingIgnoreCase_retrouveLeSpectacleSansTenirCompteDeLaCasse`
   Vérifie que la recherche par titre ignore les majuscules/minuscules.
   Justification : c'est une règle attendue d'un moteur de recherche simple côté utilisateur.

2. `findByDateHeureAfterAndStatut_neRetourneQueLesSpectaclesDisponiblesAFutureDate`
   Vérifie que la requête repository ne retourne que les spectacles futurs et disponibles.
   Justification : cette requête est utilisée pour la liste des spectacles à venir, donc le filtrage doit être fiable.

## Test de contexte

### `PestacleAppApplicationTests`

Fichier : `src/test/java/com/example/pestacle_app/PestacleAppApplicationTests.java`

Test présent :

1. `contextLoads`
   Vérifie que le contexte Spring démarre avec le profil de test.
   Justification : ce test détecte rapidement un problème global de configuration, de bean ou de démarrage.


