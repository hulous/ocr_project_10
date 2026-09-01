# Your Car Your Way — PoC Fonctionnalité de tchat

> Preuve de concept technique validant la faisabilité d'un canal de
> communication en temps réel (tchat client / support) sur l'architecture
> cible retenue pour la nouvelle application Your Car Your Way.

## 🎯 Objectif de ce dépôt

Ce dépôt ne couvre **pas** l'ensemble du périmètre fonctionnel de
l'application Your Car Your Way (cf. [cahier des charges](docs/Cahier_des_charges_YourCarYourWay_v2.odt)).
Il se limite à une **preuve de concept (PoC)** portant uniquement sur la
fonctionnalité de tchat, afin de :

- valider que l'architecture proposée (Spring Boot + Angular, cf.
  [proposition d'architecture](docs/Proposition_architecture_YourCarYourWay.odt))
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
| [`docs/Cahier_des_charges_YourCarYourWay_v2.odt`](docs/Cahier_des_charges_YourCarYourWay_v2.odt) | Besoins fonctionnels consolidés, user stories, critères d'acceptation |
| [`docs/Proposition_architecture_YourCarYourWay.odt`](docs/Proposition_architecture_YourCarYourWay.odt) | Audit de l'existant, architecture cible, modèle de données, choix technologiques |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Résumé technique rapide (lecture 5 minutes) à destination des développeurs |
| [`docs/POC_CHAT.md`](docs/POC_CHAT.md) | Périmètre précis, scénario et critères de réussite du PoC |

## 🗂️ Structure du dépôt

```
your-car-your-way-poc-chat/
├── backend/            # API Spring Boot (Java 21) — module du PoC tchat
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

- **Backend** : Java 21, Spring Boot, Spring Security (JWT), Spring Data
  JPA, Liquibase, MapStruct
- **Frontend** : Angular
- **Base de données** : PostgreSQL
- **Conteneurisation** : Docker (mise en place prévue en milestone
  « Environnement de développement », cf. [gestion de projet](#-gestion-de-projet))

## 🚀 Démarrer sur le projet

Le backend Spring Boot et le frontend Angular sont en place. La stack peut être démarrée depuis la racine du dépôt avec Docker Compose.

1. Vérifiez les variables d'environnement dans `.env`, notamment `MAIN_APP_PORT` et `FRONTEND_ORIGIN`.
2. Lancez la stack :

```bash
docker compose up --build
```

3. Ouvrez :
   - `http://localhost:4250` pour l'interface Angular
   - `http://localhost:8050` pour l'API backend

### Démarrage alternatif

#### Backend

Depuis le dossier `backend` :

```bash
mvn spring-boot:run
```

> Si `./mvnw` ne fonctionne pas, utilisez un Maven système installé localement.

#### Frontend

Depuis le dossier `frontend` :

```bash
npm install
npm start
```

## Ports et endpoints

- Backend API : `http://localhost:8050`
- Frontend Angular : `http://localhost:4250`
- Swagger/OpenAPI UI : `http://localhost:8050/swagger-ui/index.html`

## Lancer la stack localement

Le projet peut désormais être démarré avec Docker Compose depuis la racine du dépôt :

```bash
docker compose up --build
```

Puis ouvrir :

- `http://localhost:4250` pour l'interface Angular
- `http://localhost:8050` pour l'API backend
- `postgres://ycyw:ycyw@localhost:5532/ycyw_chat_app` pour la base PostgreSQL

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
