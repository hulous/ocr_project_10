# Workflows CI/CD

Aucun workflow GitHub Actions n'est encore défini dans ce dépôt.

Les contrôles disponibles localement sont exécutables dans Docker depuis la
racine :

```bash
make test
make lint-back
make lint-front
make test-e2e
```

`make test-e2e` démarre PostgreSQL, le backend et le frontend avec Compose,
puis exécute le conteneur Playwright via le profil `e2e`. Ces commandes
constituent la base à automatiser lorsqu'un pipeline CI sera ajouté.
