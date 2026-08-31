DOCKER = docker
COMPOSE = $(DOCKER) compose
ROOT = $(CURDIR)
MAKE_CMD = $(MAKE)
BACK_TEST_IMAGE = maven:3.9.9-eclipse-temurin-21
FRONT_TEST_IMAGE = node:22-bullseye-slim

.DEFAULT_GOAL := help

help: ## Show this help
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

ps: ## List local services
	$(COMPOSE) ps

logs: ## Follow local service logs
	$(COMPOSE) logs -f

build: ## Build local services
	$(COMPOSE) build

up: ## Start app local services
	$(COMPOSE) up

upd: ## Start app local services in detached mode
	$(COMPOSE) up -d

run: ## Build and run the app and database through Docker Compose
	$(COMPOSE) up --build

down: ## Stop local services
	$(COMPOSE) down

test-back: ## Run backend unit tests in Docker
	$(DOCKER) run --rm -v $(ROOT)/backend:/workspace -w /workspace $(BACK_TEST_IMAGE) mvn -q test

test-front: ## Run frontend unit tests in Docker
	$(DOCKER) run --rm -v $(ROOT)/frontend:/workspace -w /workspace $(FRONT_TEST_IMAGE) bash -lc "apt-get update && apt-get install -y chromium && npm install && npm test -- --watch=false --browsers=ChromeHeadlessNoSandbox"

lint-back: ## Run backend lint and formatting checks in Docker
	$(DOCKER) run --rm -v $(ROOT)/backend:/workspace -w /workspace $(BACK_TEST_IMAGE) mvn -q verify

lint-front: ## Run frontend lint and formatting checks in Docker
	$(DOCKER) run --rm -v $(ROOT)/frontend:/workspace -w /workspace $(FRONT_TEST_IMAGE) bash -lc "npm install && npm run lint && npm run format:check"

test: ## Run backend and frontend tests in Docker
	$(MAKE_CMD) test-back
	$(MAKE_CMD) test-front

clean: ## Stop local services and remove build artifacts
	$(COMPOSE) down --volumes --remove-orphans
	-rm -rf backend/target frontend/node_modules frontend/dist
