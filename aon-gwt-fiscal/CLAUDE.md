# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Module Purpose

`aon-gwt-fiscal` is the main GWT-based frontend module for fiscal and financial operations in the AON ERP. It covers:
- Accounting (general ledger, trial balance, statements)
- Spanish tax declarations (MOD111, MOD115, MOD130, MOD140, MOD180, MOD303, MOD347, MOD349, MOD390, MOD425, etc.)
- Invoice management (VAT, IRPF, SII integration)
- Financial operations (payments, settlements, bank integration)
- Registry (customers, suppliers, creditors, products)

## Build Commands

```bash
# Build this module only
mvn clean install -DskipTests -pl aon-gwt-fiscal

# Build with GWT compilation (slow, ~minutes)
mvn clean install -pl aon-gwt-fiscal

# Build in dev mode (faster GWT compilation)
mvn clean install -DskipTests -Dgwt.working=true -pl aon-gwt-fiscal

# Build this module + all dependents
mvn -pl aon-gwt-fiscal --also-make-dependents clean install -DskipTests
```

See root `CLAUDE.md` for full build documentation, test DB config, and project-wide conventions.

## GWT Architecture

The module uses GWT RPC with a strict 3-layer pattern. Every service has 4 pieces:

1. **`*Service.java`** — Client interface extending `RemoteService`, annotated with `@RemoteServiceRelativePath("roms/ServiceName")`
2. **`*ServiceAsync.java`** — Async mirror of the interface (each method takes `AsyncCallback<T>`)
3. **`*ServiceAsyncDecorator.java`** — Wraps the async interface; calls `AON.start()` before each RPC and wraps callbacks in `AsyncCallbackWrapper` for centralized error handling
4. **`*ServiceImpl.java`** (server package) — Extends `AonStatelessRemoteServiceServlet`, annotated with `@WebServlet`, delegates to `ACCOUNTING.*` or other aon-occam API methods

When adding a new RPC method, update all 4 files.

## Entry Points & Module System

- **`MainEntryPoint.java`** (~1,200 lines) — single GWT entry point; reads URL params to route to the appropriate sub-module
- Each functional area has a `*Module.java` (implements `EntryPoint` or is invoked from `MainEntryPoint`) and a `*ModuleOptions.java` (fluent configuration object)
- Code splitting via `GWT.runAsync()` keeps initial payload small

## Key Patterns

**Async decorator pattern** — Never call raw `*ServiceAsync` directly. Always use the decorator:
```java
private static final FooServiceAsync SERVICE;
static {
    SERVICE = new FooServiceAsyncDecorator(GWT.create(FooService.class));
}
```

**Fiscal model variants** — Each MOD has authority-specific subclasses:
- `AEAT` (national), `Araba`/`Bizkaia`/`Gipuzkoa` (Basque Country), `Navarra`
- Base class: `Model*Base` → regional: `Model*` or `Model*Region`

**Streaming exports** — Large downloads use dedicated `*StreamServlet` (avoid OOM). Excel via Apache POI (`*ExcelAction`), PDF via OpenPDF/iText.

**Multi-tenancy** — Every server call receives an `Occam` context object (wraps `domainName`, `domainId`, `user`). The client builds it via `EntryPointUtils` from localStorage. Never skip this context.

## GWT Module Config

- Production: `Fiscal.gwt.xml` — locale `es_ES`, entry point `MainEntryPoint`
- Development: `DevelopmentFiscal.gwt.xml` (activated with `-Dgwt.working=true`)
- GWT version: 2.12.2 (Jakarta)

## Package Layout

```
client/
  ├── accounting/       General ledger, statement panels, period management
  ├── finance/          Payments, settlements, bank integration (Nordigen, CheckIt)
  ├── invoice/          VAT/IRPF reports, invoice console, SII
  ├── mod111…mod425/    One package per Spanish tax model
  ├── registry/         Customer, supplier, creditor modules
  ├── product/          Product and catalogue modules
  ├── config/           Fiscal configuration
  ├── sii/              SII entry point
  └── widget/           Shared custom GWT widgets
server/
  ├── *ServiceImpl.java         RPC servlet implementations
  ├── *StreamServlet.java       Streaming download servlets
  ├── *ExcelAction.java         Excel export handlers
  └── invoice/                  Invoice-specific server logic
```

## Tests

Legacy tests are under `src/test/java/old/` and require a live MariaDB instance. See root `CLAUDE.md` for DB connection parameters.
