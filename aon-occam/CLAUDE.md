# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build the module
mvn clean install -DskipTests

# Run all tests (via the OccamTestSuite)
mvn test

# Run a specific test class
mvn test -Dtest=ClassName

# Run with test DB configuration overrides
mvn test -DdbHost=127.0.0.1 -DdbPort=3306 -DdbName=aon_jooq_ -DdbUser=dbuser -DdbPasswd=serubd2000

# Run with a specific domain
mvn test -DdomainName=occamtest.aonsolutions.test -DdomainUser=admin
```

Tests require a live MariaDB/MySQL database. Default connection: `127.0.0.1:3306`, DB prefix `aon_jooq_`, user `dbuser`, password `serubd2000`. Tests run through `OccamTestSuite` (the only suite configured in surefire).

## Architecture Overview

**aon-occam** is the core business logic module of the AON ERP/accounting platform. It follows a strict API/implementation split:

### Package Structure

- `com.esferalia.aon.occam.api` — Public API: interfaces (`IFiscal`, `IRelationship`, etc.), model POJOs, JSON transfer objects
  - `.api.model` — Domain model classes (POJOs for entities like `Company`, `Invoice`, `Enterprise`, etc.)
  - `.api.model.invoice` — Invoice communication model (SII, VeriFACTU integration)
  - `.api.model.fiscal` — Fiscal model data (IRPF, VAT breakdowns, model declarations)
  - `.api.model.doc` — Document attachment model (S3/external storage)
  - `.api.json` — JSON transfer objects (named `*JSON`) used for REST serialization
  - `.api.fiscal` — Fiscal declaration model interfaces and base types (MODEL130, MODEL303, etc.)

- `com.esferalia.aon.occam.impl.jooq` — jOOQ-based implementations
  - `.impl.jooq.*Impl` — Top-level service implementations (e.g. `FiscalImpl`, `RelationshipImpl`)
  - `.impl.jooq.dao` — DAO classes that issue actual SQL via jOOQ DSL
  - `.impl.jooq.dao.accounting` — Accounting-specific DAOs (analytical accounting, JAXB)
  - `.impl.jooq.console` — Admin/console utilities (domain cleanup, integrity checks)

- `com.esferalia.aon.occam.server` — Server-side utilities
  - `.server.accounting` — Balance scripts (BOE balance sheets, P&L)
  - `.server.fiscal` — Fiscal calculation and file format writers per year/authority
  - `.server.fiscal.format.mod*` — File format writers for each fiscal model (mod111, mod303, etc.) by year and authority (AEAT, BIZKAIA, GIPUZKOA, ARABA, NAVARRA)

- `com.esferalia.aon.jooq.extension` — jOOQ utility extensions (`DSLExtensions` for `hex`/`unhex`/`uuid` helpers)

### Key Concepts

**AONContext** (`api/AONContext.java`) is the central context object passed throughout the system. It wraps a jOOQ `DSLContext` + DB `Connection` and carries `domainName`, `domainId`, and `user`. Always use try-with-resources since `CloseableAONContext` implements `AutoCloseable`:
```java
try (AONContext.CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, user)) {
    // use ctx
}
```

**Multi-tenancy**: Data is isolated by `domain` (integer ID). All DAOs filter by domain. The `global-aonsolutions-net` schema holds cross-domain data.

**Database**: MySQL/MariaDB. jOOQ is used for all database access with `RenderSchema=false` and `INLINED` params. jOOQ-generated table classes live in a dependency (`aon.master.jooq`), not in this module.

**Fiscal models**: Spanish tax declarations (AEAT and Basque Country foral authorities). Each declaration type has:
- An interface in `api/fiscal/` (e.g. `IMODEL303`)
- A concrete data class (e.g. `MODEL303`)
- DAO(s) in `impl/jooq/dao/` to build the data
- File writers in `server/fiscal/format/mod*/` — one class per year/authority combination

**MVEL expressions**: Some fiscal calculations use MVEL expression language (`ModelMVELContext`, `OLDMod130MVELContext`) for configurable formula evaluation.

### Test Infrastructure

All tests extend `AbstractOccamTest`, which:
- Connects to a real database using system properties
- Creates/reuses a test domain (`occamtest.aonsolutions.test` by default)
- Provides a shared `CloseableAONContext ctx` per test class

The test suite entry point is `OccamTestSuite`. Tests use JUnit 4, Hamcrest, AssertJ, and javafaker for test data generation.
