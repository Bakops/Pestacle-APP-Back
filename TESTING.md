# Stratégie de tests API

Ce projet est configuré avec 3 types de tests JUnit 5 via des tags:

- `unit` : tests unitaires (services/contrôleurs)
- `perf` : tests de performance (latence moyenne)
- `load` : tests de charge (requêtes concurrentes)

## Lancer les tests

Depuis le dossier backend `Pestacle-APP-Back`:

```bash
./mvnw test -Punit-tests
./mvnw test -Pperf-tests
./mvnw test -Pload-tests
```

Pour lancer toutes les suites ensemble:

```bash
./mvnw test -Pall-tests
```

## Exécution par défaut

Sans profil, Maven exécute les tests non taggés + unitaires, et exclut `perf` + `load`.

## Fichiers de tests ajoutés

- `src/test/java/com/example/pestacle_app/service/SpectacleServiceUnitTest.java`
- `src/test/java/com/example/pestacle_app/controller/SpectacleControllerUnitTest.java`
- `src/test/java/com/example/pestacle_app/controller/SpectacleControllerPerformanceTest.java`
- `src/test/java/com/example/pestacle_app/controller/SpectacleControllerLoadTest.java`
