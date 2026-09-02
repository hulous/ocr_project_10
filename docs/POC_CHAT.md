# PoC — Fonctionnalité de tchat

## Pourquoi ce PoC

Le cahier des charges consolidé identifie un besoin d'assistance
(« US-20 : disposer d'un moyen de contacter le support ») dont le canal
précis restait à confirmer avec le métier. Un tchat en temps réel entre
un client et le support est l'option la plus exigeante techniquement
parmi les canaux envisageables : elle est retenue comme périmètre de
preuve de concept car elle permet de valider, sur l'architecture cible,
un flux que les autres fonctionnalités (toutes en REST classique) ne
couvrent pas.

## Ce que le PoC doit démontrer

- L'API backend (Spring Boot) peut exposer un canal de communication
  temps réel (WebSocket/STOMP) en plus de son API REST, sans remettre
  en cause le découpage en modules retenu.
- Le frontend (Angular) peut se connecter à ce canal et afficher les
  messages échangés en direct.
- L'authentification déjà prévue pour l'API REST (JWT) peut être
  réutilisée pour sécuriser l'accès au canal temps réel.

## Scénario disponible

1. Un visiteur qui ouvre `/chat` est redirigé vers `/login`.
2. Le visiteur crée un compte sur `/register`, puis se connecte.
3. L'application charge l'historique de la conversation `demo` via
  `GET /api/conversations/demo/messages`.
4. Elle ouvre une connexion SockJS/STOMP sur `/ws`, s'abonne à
  `/topic/conversations/demo`, puis publie les messages vers
  `/app/chat.send`.
5. Le backend associe le message à l'utilisateur authentifié et le diffuse
  aux clients abonnés à la conversation.

L'identifiant de conversation peut aussi être fourni par la route
`/chat/{conversationId}`. Le proxy Angular relaie `/api` et `/ws` vers le
backend en environnement de développement.

## Ce que le PoC ne couvre pas

- L'historisation longue durée des conversations, la recherche dans
  l'historique, les pièces jointes.
- Le routage vers un agent de support précis ou une file d'attente.
- L'intégration avec un outil de support existant (helpdesk, etc.).

Ces points restent hors périmètre : le PoC a une vocation de
validation technique, pas de livraison d'une fonctionnalité complète.

## Critères de réussite

- Un message envoyé depuis le frontend est reçu côté backend puis
  restitué en temps réel à un autre client connecté sur la même
  conversation.
- Le canal est accessible uniquement à un utilisateur authentifié.
- Les tests unitaires backend/frontend et le scénario E2E d'authentification
  et d'ouverture du tchat passent.
- Le bilan technique documente les éventuelles limites rencontrées et les
  recommandations pour un passage à l'échelle sur le périmètre complet de
  l'application.

## Vérification

Depuis la racine du dépôt :

```bash
make test
make test-e2e
```

## Suivi

Le détail des tâches est suivi via les issues du milestone « PoC — Fonctionnalité de tchat » sur GitHub.
