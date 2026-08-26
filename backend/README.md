# Backend — PoC Tchat

Module backend du PoC, en Java 21 / Spring Boot.

> ⚠️ Ce dossier est actuellement vide de code : il ne contient que la
> structure attendue. Le code sera ajouté au fil des issues du milestone
> « PoC — Fonctionnalité de tchat ».

## Contenu attendu (à venir)

- API REST minimale (authentification réutilisant les principes JWT
  décrits dans `docs/ARCHITECTURE.md`)
- Endpoint temps réel (WebSocket / STOMP) pour l'échange de messages du
  tchat
- Configuration Liquibase pour le schéma minimal nécessaire au PoC
  (table de messages)
- Tests automatisés (unitaires a minima sur la logique métier du tchat)

## Conventions

- Organisation du code par fonctionnalité (« package by feature »),
  conformément à la proposition d'architecture.
- Un module = un package racine (ex. `chat`), pas de découpage
  technique global (`controllers`, `services`, `repositories` en
  vrac à la racine).
