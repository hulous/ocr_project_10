# Your Car Your Way — PoC Fonctionnalité de tchat

> Preuve de concept technique validant la faisabilité d'un canal de
> communication en temps réel (tchat client / support) sur l'architecture
> cible retenue pour la nouvelle application Your Car Your Way.

## 🎯 Objectif de ce dépôt

Ce dépôt ne couvre **pas** l'ensemble du périmètre fonctionnel de
l'application Your Car Your Way. Le cahier des charges et la proposition
d'architecture de référence sont des documents externes au dépôt ; le
résumé technique local est disponible dans
[`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).
Il se limite à une **preuve de concept (PoC)** portant uniquement sur la
fonctionnalité de tchat, afin de :

- valider que l'architecture proposée (Spring Boot + Angular, cf. le
  [résumé d'architecture](docs/ARCHITECTURE.md))
  supporte un flux temps réel, en plus des échanges REST classiques ;
- donner à l'équipe un exemple concret de la structure de code et des
  conventions attendues avant d'attaquer le développement du reste de
  l'application ;
- servir de support d'onboarding pour un développeur qui rejoint le
  projet.

Ce dépôt contient aujourd'hui une preuve de concept fonctionnelle avec un backend Spring Boot et un frontend Angular pour la fonctionnalité de tchat.

## 📚 Documentation

| Document | Contenu |
|---|---|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Résumé technique rapide (lecture 5 minutes) à destination des développeurs |
| [`docs/POC_CHAT.md`](docs/POC_CHAT.md) | Périmètre précis, scénario et critères de réussite du PoC |

Les documents fonctionnels et la proposition d'architecture complète sont
gérés séparément et ne sont pas versionnés dans ce dépôt.

## 🗂️ Structure du dépôt

```
your-car-your-way-poc-chat/
├── backend/            # API Spring Boot (Java 24) — module du PoC tchat
├── frontend/           # Application Angular — interface du PoC tchat
├── docs/               # Documentation fonctionnelle et technique
│   └── diagrams/       # Diagrammes UML (composants, déploiement, classes)
├── .github/            # Templates d'issues/PR, workflows CI
└── README.md
```

Le détail de ce qui est attendu dans `backend/` et `frontend/` est décrit
dans le `README.md` de chacun de ces dossiers.

## 🧱 Stack technique retenue

Cohérente avec la proposition d'architecture (voir `docs/ARCHITECTURE.md`
pour le détail et la justification des choix) :

- **Backend** : Java 24, Spring Boot, Spring Security (JWT), Spring Data
  JPA, Liquibase, MapStruct
- **Frontend** : Angular
- **Base de données** : PostgreSQL
- **Conteneurisation** : Docker Compose pour l'environnement local, avec
  PostgreSQL, le backend, le frontend et un profil Playwright E2E

## 🚀 Démarrer sur le projet

Le chemin recommandé est Docker Compose depuis la racine du dépôt. Le fichier
`.env` est requis par PostgreSQL et le backend.

1. Créez le fichier d'environnement à partir de l'exemple :

```bash
cp backend/.env.sample.properties .env
```

2. Remplacez au minimum `JWT_SECRET_TOKEN` par une valeur aléatoire forte,
  puis lancez la stack :

```bash
docker compose up --build
```

Puis ouvrir :

- `http://localhost:4250` pour l'interface Angular
- `http://localhost:8050` pour l'API backend
- `postgres://ycyw:ycyw@localhost:5532/ycyw_chat_app` pour la base PostgreSQL

Swagger/OpenAPI est disponible à
`http://localhost:8050/swagger-ui/index.html`.
L'état de santé du backend est disponible à
`http://localhost:8050/actuator/health`.

### Commandes Make

Depuis la racine, `make help` affiche les commandes disponibles. Les plus
utiles sont :

| Commande | Rôle |
|---|---|
| `make run` | Construire et démarrer la stack |
| `make upd` | Démarrer la stack en arrière-plan |
| `make ps` | Afficher l'état des services |
| `make logs` | Suivre les journaux |
| `make test` | Exécuter les tests backend et frontend dans Docker |
| `make test-e2e` | Construire la stack et exécuter les tests Playwright |
| `make lint-back` / `make lint-front` | Vérifier le formatage et le lint |
| `make down` | Arrêter les services |

### Exécution directe

L'exécution hors Docker reste possible avec Java 24+, Maven 3.8+, Node.js et
npm installés localement. Dans ce cas, PostgreSQL doit être accessible sur
`localhost:5532` (ou les variables `POSTGRES_*` doivent être adaptées), et
`MAIN_APP_PORT` doit être défini à `8050` pour conserver les URLs ci-dessous.
Lancez `mvn spring-boot:run` dans `backend/`, puis `npm install && npm start`
dans `frontend/`. Le frontend écoute alors sur `4200`; le proxy de
développement relaie `/api` et `/ws` vers le backend.

## Ports, routes et protocole

- Frontend Angular via Compose : `http://localhost:4250`
- Backend API via Compose : `http://localhost:8050`
- PostgreSQL via Compose : `localhost:5532`
- Authentification : `POST /api/auth/register`, `POST /api/auth/login`,
  `GET /api/auth/me`
- Historique : `GET /api/conversations/{conversationId}/messages`
- WebSocket SockJS/STOMP : endpoint `/ws`, publication `/app/chat.send`,
  abonnement `/topic/conversations/{conversationId}`

Les routes API protégées et la connexion STOMP nécessitent un JWT d'accès.
Le login renvoie ce jeton et sa durée d'expiration ; aucun mécanisme de
refresh token n'est implémenté dans ce PoC. Le client
envoie le jeton dans l'en-tête `Authorization` de la requête REST et dans les
headers STOMP de connexion.

## 🗓️ Gestion de projet

La feuille de route est suivie via les **milestones** et **issues**
GitHub du dépôt, organisées en 4 jalons correspondant aux étapes de la
mission :

1. ✅ Cadrage fonctionnel (cahier des charges & user stories)
2. ✅ Audit technique & proposition d'architecture
3. 🔧 Mise en place de l'environnement de développement
4. 🔧 PoC — Fonctionnalité de tchat

Les issues et milestones du projet sont suivies directement sur GitHub.

## 👤 Contact

Projet mené par Fabien ([@hulous](https://github.com/hulous)) dans le
cadre de la certification RNCP41330 (OpenClassrooms).
