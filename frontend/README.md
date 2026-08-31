# Frontend — PoC Tchat

Module frontend du PoC, en Angular.

Ce dossier contient un début d'application Angular avec un module `chat/` et un composant de tchat.

## Contenu actuel

- Angular 17 application scaffoldée
- Module `src/app/chat/` créé
- Application servie sur le port `4200` en développement

## Démarrage

Depuis le dossier `frontend` :

```bash
npm install
npm start
```

Ou depuis la racine du dépôt :

```bash
docker compose up --build
```

## Objectifs du PoC frontend

- Afficher une interface de tchat minimale
- Se connecter au backend Spring Boot
- Utiliser le canal temps réel exposé par le backend
- Respecter les principes d'accessibilité définis dans le cahier des charges

## Ports et endpoints

- Frontend Angular : http://localhost:4250
- Backend API : http://localhost:8050
- Swagger/OpenAPI UI : http://localhost:8050/swagger-ui/index.html

## Conventions

- Un module Angular dédié au tchat (`chat/`)
- Composants du PoC regroupés sous `src/app/chat/`
