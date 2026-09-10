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

Ce dépôt contient une preuve de concept fonctionnelle avec un backend Spring Boot et un frontend Angular pour l'authentification, les utilisateurs et la fonctionnalité de tchat. Le code applicatif est présent et exécutable sur la branche `devs`.

**Important :** ce n'est pas l'application complète Your Car Your Way. Les
modules catalogue, réservation, paiement et notification appartiennent à
l'architecture cible, mais ne sont pas livrés dans ce PoC.

## 📚 Documentation

| Document | Contenu |
|---|---|
| [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) | Résumé technique rapide (lecture 5 minutes) à destination des développeurs |
| [`docs/POC_CHAT.md`](docs/POC_CHAT.md) | Périmètre précis, scénario et critères de réussite du PoC |

Le dépôt ne contient pas encore de `CONTRIBUTING.md` ; les conventions
techniques utiles sont décrites dans [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md)
et dans les README des modules.

## 🗂️ Structure du dépôt

```
your-car-your-way-poc-chat/
├── backend/
│   ├── pom.xml         # Dépendances et build Maven, Java 24 / Spring Boot 4.0.6
│   ├── .env.sample.properties # Paramètres PostgreSQL et secret JWT à recopier dans .env
│   └── src/
│       ├── main/java/com/ycyw/chatapi/ # contrôleurs, services, sécurité, JPA
│       ├── main/resources/             # application.yml et migrations Liquibase
│       └── test/                       # tests Spring et intégration WebSocket
├── frontend/
│   ├── package.json    # Angular 21, tests Vitest et commandes npm
│   └── src/app/         # Routes login/register/chat et interface du tchat
├── e2e/
│   └── tests/           # Scénarios Playwright exécutés dans Docker
├── docs/                # Documentation fonctionnelle et technique
│   └── diagrams/        # Diagrammes UML
├── compose.yml          # PostgreSQL, backend, frontend et profil E2E
├── Makefile             # Raccourcis de développement et de test
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

## 🚀 Démarrage rapide

Le chemin recommandé est Docker Compose depuis la racine du dépôt. Docker
Compose lit les paramètres PostgreSQL et backend dans le fichier `.env`.
Vérifiez que Docker Engine ou Docker Desktop est démarré avant de lancer les
commandes suivantes.

1. Créez le fichier d'environnement à partir de l'exemple :

```bash
cp backend/.env.sample.properties .env
```

2. Remplacez au minimum `JWT_SECRET_TOKEN` par une valeur aléatoire forte,
   puis lancez les services :

```bash
docker compose up --build
```

Le premier démarrage peut prendre plusieurs minutes, car les images backend et
frontend sont construites et les dépendances sont installées. Gardez ce
terminal ouvert pour suivre les journaux. Pour démarrer en arrière-plan,
utilisez `make upd` ou `docker compose up --build -d`.

3. Attendez que PostgreSQL, le backend et le frontend soient démarrés, puis
  ouvrez :

- `http://localhost:4250` pour l'interface Angular
- `http://localhost:8050/swagger-ui/index.html` pour explorer l'API REST
- `http://localhost:8050/actuator/health` pour vérifier la santé du backend
- PostgreSQL sur `localhost:5532` (base `ycyw_chat_app`)

Le résultat attendu est une interface Angular accessible sur le port `4250`,
un backend signalant `UP` sur `/actuator/health`, et une base PostgreSQL
accessible sur le port `5532`. Le démarrage crée les tables nécessaires via
Liquibase.

Swagger/OpenAPI est disponible à
`http://localhost:8050/swagger-ui/index.html` ; il décrit les endpoints REST,
mais le flux temps réel se teste depuis l'interface Angular.

### Scénario manuel du tchat

Une fois l'interface ouverte :

1. Ouvrez `http://localhost:4250/chat`. Comme vous n'êtes pas connecté, vous
  êtes redirigé vers `/login`.
2. Ouvrez le lien d'inscription, ou directement
  `http://localhost:4250/register`, puis créez un compte avec un nom, une
  adresse e-mail et un mot de passe.
3. Revenez à la page de connexion, saisissez ces identifiants et cliquez sur
  **Se connecter**. Vous êtes redirigé vers `/chat`.
4. La conversation `demo` se charge automatiquement avec son historique.
5. Saisissez un message et cliquez sur **Envoyer**. Le message apparaît dans
  le journal du tchat.

Le navigateur utilise une connexion **SockJS/STOMP** : SockJS aide à établir
la connexion temps réel et STOMP définit le format des messages. Le backend
la protège avec un **JWT**, c'est-à-dire un jeton temporaire remis après la
connexion. L'historique passe par `GET /api/conversations/demo/messages` ; les
messages temps réel utilisent `/ws`, `/app/chat.send` et
`/topic/conversations/demo`.

Le PoC ne fournit pas encore de refresh token (jeton permettant d'obtenir un
nouveau JWT sans se reconnecter), ni de recherche, pièces jointes ou routage
vers un agent de support.

### Fonctionnalités REST disponibles

- `POST /api/auth/register` : créer un compte (le mot de passe est stocké avec
  un hachage bcrypt).
- `POST /api/auth/login` : se connecter et obtenir un JWT, c'est-à-dire un
  jeton signé utilisé pour authentifier les requêtes suivantes.
- `GET /api/auth/me` : récupérer l'utilisateur connecté.
- `GET /api/user/{id}` : récupérer un utilisateur authentifié.
- `GET /api/conversations/{conversationId}/messages` : récupérer l'historique
  d'une conversation.

### Commandes Make

Depuis la racine, `make help` affiche les commandes disponibles. Les plus
utiles sont :

| Commande | Rôle |
|---|---|
| `make run` | Construire et démarrer la stack |
| `make upd` | Démarrer la stack en arrière-plan |
| `make ps` | Afficher l'état des services |
| `make logs` | Suivre les journaux |
| `make test` | Exécuter les tests backend et frontend dans Docker (le frontend couvre actuellement 25 tests) |
| `make test-e2e` | Réinitialiser la stack, la construire et lancer les 3 scénarios Playwright : redirection vers login, inscription/connexion et échange temps réel entre deux utilisateurs |
| `make lint-back` / `make lint-front` | Vérifier le formatage et le lint |
| `make down` | Arrêter les services |

Pour arrêter une stack lancée au premier plan, utilisez `Ctrl+C`, puis :

```bash
docker compose down
```

Pour vérifier l'état des services ou consulter les journaux :

```bash
docker compose ps
docker compose logs -f
```

### Dépannage rapide

- **Le fichier `.env` est introuvable** : exécutez `cp
  backend/.env.sample.properties .env` depuis la racine, puis relancez Compose.
- **Un port est déjà utilisé** : arrêtez l'application qui utilise `4250`,
  `8050` ou `5532`, ou modifiez le port exposé dans `compose.yml` et les URLs
  correspondantes dans ce README.
- **Un service ne démarre pas** : consultez `docker compose ps`, puis
  `docker compose logs backend`, `docker compose logs frontend` ou
  `docker compose logs postgres`.
- **La base doit être recréée** : la commande suivante arrête les services et
  supprime le volume PostgreSQL local ; les données de développement seront
  perdues :

  ```bash
  docker compose down --volumes --remove-orphans
  docker compose up --build
  ```

- **Docker refuse de construire les images** : vérifiez que Docker est démarré,
  que le dépôt est bien ouvert depuis sa racine et que vous disposez d'une
  connexion réseau pour télécharger les images et dépendances.

### Prérequis

- Docker Engine avec Docker Compose, capable de construire les images utilisées
  par `compose.yml`.
- Pour les commandes Make de test, Docker suffit : les tests utilisent Maven
  avec Eclipse Temurin 24 et Node.js 22 dans des conteneurs.
- Pour une exécution hors Docker (optionnelle), Java/JDK 24, Maven 3.9.9,
  Node.js 22 et npm sont nécessaires. Le frontend écoute alors sur `4200`.

## Ports, routes et protocole

- Frontend Angular via Compose : `http://localhost:4250`
- Backend API via Compose : `http://localhost:8050`
- PostgreSQL via Compose : `localhost:5532`
- Authentification : `POST /api/auth/register`, `POST /api/auth/login`,
  `GET /api/auth/me`
- Utilisateurs : `GET /api/user/{id}`
- Historique : `GET /api/conversations/{conversationId}/messages`
- WebSocket SockJS/STOMP : endpoint `/ws`, publication `/app/chat.send`,
  abonnement `/topic/conversations/{conversationId}`

Les routes API protégées et la connexion STOMP nécessitent un JWT d'accès.
Le login renvoie ce jeton et sa durée d'expiration ; aucun mécanisme de
refresh token n'est implémenté dans ce PoC. Le client
envoie le jeton dans l'en-tête `Authorization` de la requête REST et dans les
headers STOMP de connexion.

## 🗓️ Statut du projet

La feuille de route est suivie via les **milestones** et **issues**
GitHub du dépôt, organisées en 4 jalons correspondant aux étapes de la
mission :

1. ✅ Cadrage fonctionnel (références externes ; non incluses dans cette branche)
2. ✅ Audit technique et documentation locale de l'architecture
3. ✅ Environnement de développement Docker Compose, PostgreSQL et configuration
4. ✅ PoC tchat : inscription, connexion JWT, historique, WebSocket temps réel,
   interface Angular, tests backend/frontend et scénarios E2E

Les modules catalogue, réservation, paiement et notification restent à faire.
Le tchat est couvert par les tests unitaires et d'intégration présents dans le
dépôt. La commande `make test-e2e` permet de lancer la validation de la stack
complète avec Docker ; son résultat dépend de l'environnement local.

Les issues et milestones du projet sont suivies directement sur GitHub.

## Contribution

Le dépôt ne contient pas encore de `CONTRIBUTING.md`. Ce document doit être
ajouté ou sa référence doit être corrigée ; en attendant, les commandes et
conventions techniques sont décrites dans ce fichier et dans les README de
`backend/` et `frontend/`.

## 👤 Contact

Projet mené par Fabien ([@hulous](https://github.com/hulous)) dans le
cadre de la certification RNCP41330 (OpenClassrooms).
