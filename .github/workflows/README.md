# Workflows CI/CD

Le workflow GitHub Actions `CI` exécute les vérifications backend et frontend
sur les pushes et pull requests vers `main` et `master`.

Les tests E2E Playwright sont disponibles via le déclenchement manuel du
workflow. Le job E2E attend que les jobs backend et frontend soient verts.

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
constituent également la commande utilisée par le job E2E manuel.
