# Java Code Generator - CQRS & DDD Architecture

Ce projet génère automatiquement les fichiers d'une application Java Spring en suivant les patterns **DDD**, **CQRS**, et une architecture pilotée par **Axon Framework**, **R2DBC** et **WebFlux**.

## ⚙️ Fonctionnalités

- Génération de :
	- Command Controllers / Handlers
	- Query Controllers / Handlers / Projections
	- DTOs, Commands, Queries, Repositories
- Basé sur des templates Mustache personnalisables
- Prise en charge des types réactifs (`Mono`, `Flux`)
- Architecture modulaire et extensible

## 🧱 Technologies

- Java 17+
- Spring Boot
- Mustache
- Lombok
# Usage

cd cqrs-generator/

```sh
cd cqrs-generator/

mvn spring-boot:run

curl -N -X POST http://localhost:8070/api/v1/generator/all \
  -H "Content-Type: application/json" \
  -H "Accept: application/x-ndjson" \
  -d '{
    "outputDir": "target/generated",
    "definition": {
      "name": "Product",
      "fields": [
        { "name": "id", "type": "String" },
        { "name": "name", "type": "String" },
        { "name": "price", "type": "Double" }
      ]
    }
  }'

```
## SEE
[tests.http](docs/tests.http)