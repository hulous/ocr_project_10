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

**Aucun code applicatif n'est présent à ce stade.** Ce dépôt contient la
structure du projet, la documentation et la gestion de projet
(milestones / issues) qui serviront de point de départ au développement.

## 📚 Documentation

| Document | Contenu |
|---|---|
| [`docs/Cahier_des_charges_YourCarYourWay_v2.odt`](docs/Cahier_des_charges_YourCarYourWay_v2.odt) | Besoins fonctionnels consolidés, user stories, critères d'acceptation |
| [`docs/Proposition_architecture_YourCarYourWay.odt`](docs/Proposition_architecture_YourCarYourWay.odt) | Audit de l'existant, architecture cible, modèle de données, choix technologiques |
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Résumé technique rapide (lecture 5 minutes) à destination des développeurs |
| [`docs/POC_CHAT.md`](docs/POC_CHAT.md) | Périmètre précis, scénario et critères de réussite du PoC |
| [`CONTRIBUTING.md`](CONTRIBUTING.md) | Conventions de contribution (branches, commits, revue de code) |

## 🗂️ Structure du dépôt

```
your-car-your-way-poc-chat/
├── backend/            # API Spring Boot (Java 21) — module du PoC tchat
├── frontend/           # Application Angular — interface du PoC tchat
├── docs/               # Documentation fonctionnelle et technique
│   └── diagrams/       # Diagrammes UML (composants, déploiement, classes)
├── scripts/            # Scripts d'outillage (gestion de projet GitHub, etc.)
├── .github/            # Templates d'issues/PR, workflows CI (à venir)
├── CONTRIBUTING.md
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

Le code n'existe pas encore. Les prochaines étapes (suivies via les
issues GitHub, cf. ci-dessous) consistent à :

1. mettre en place le squelette Spring Boot dans `backend/` ;
2. mettre en place le squelette Angular dans `frontend/` ;
3. configurer `docker-compose` (backend, frontend, PostgreSQL) ;
4. configurer la CI de base (build + lint).

Une fois ces étapes réalisées, cette section sera mise à jour avec les
commandes exactes pour lancer le projet en local.

## 🗓️ Gestion de projet

La feuille de route est suivie via les **milestones** et **issues**
GitHub du dépôt, organisées en 4 jalons correspondant aux étapes de la
mission :

1. ✅ Cadrage fonctionnel (cahier des charges & user stories)
2. ✅ Audit technique & proposition d'architecture
3. 🔧 Mise en place de l'environnement de développement
4. 🔧 PoC — Fonctionnalité de tchat

Le script [`scripts/setup_project_management.sh`](scripts/setup_project_management.sh)
crée automatiquement ces milestones, les labels et les issues associées
via le [GitHub CLI](https://cli.github.com/) (`gh`). Voir l'en-tête du
script pour le mode d'emploi.

## 👤 Contact

Projet mené par Fabien ([@hulous](https://github.com/hulous)) dans le
cadre de la certification RNCP41330 (OpenClassrooms).
