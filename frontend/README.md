# Frontend — PoC Tchat

Module frontend du PoC, en Angular.

Ce dossier contient l'application Angular du PoC, avec les écrans
d'inscription, de connexion et de tchat.

## Contenu actuel

- Application Angular 17
- Routes `/login`, `/register` et `/chat` protégées par authentification
- Chargement de l'historique via REST
- Messages temps réel via RxStomp, SockJS et STOMP
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

## Tests et qualité

Les commandes peuvent être lancées dans Docker depuis la racine :

```bash
make test-front
make lint-front
```

Les tests E2E couvrent la redirection d'un visiteur non authentifié, la
création d'un compte, la connexion et l'ouverture du tchat :

```bash
make test-e2e
```

## Ports et endpoints

- Frontend Angular : http://localhost:4250
- Backend API : http://localhost:8050
- Swagger/OpenAPI UI : http://localhost:8050/swagger-ui/index.html
- Proxy de développement : `/api` et `/ws` sont relayés vers le backend

## Conventions

- Un module Angular dédié au tchat (`src/app/chat/`)
- Services transverses sous `src/app/core/`
- Pages d'authentification sous `src/app/pages/`
