# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

### Full build (Java + frontend)
```bash
# From aon-web-aio/
mvn clean install -DskipTests
```

### Frontend only (faster iteration)
```bash
# From aon-web-aio/ — runs webpack via npm
cd src/main/webapp && npm run build

# Or directly via webpack from project root
npx webpack --config webpack.config.js
```

The frontend-maven-plugin installs Node v20.7.0 and runs `npm install` + `npm run build` automatically during `mvn package`. The working directory for npm is `src/main/webapp/`.

### No Java tests in this module
This module depends on `aon.tests` from `aon-api` for test infrastructure. Run backend tests from `aon-occam` or `aon-api` instead.

## Architecture Overview

**aon-web-aio** is the main deployable WAR for the AON ERP web application. It packages a Java servlet backend with a Webpack-bundled vanilla Web Components frontend.

### Frontend Structure (`src/main/webapp/`)

The frontend uses **native Web Components** (no framework). All custom elements extend `AonElement` (`components/AonElement.js`), which itself extends `HTMLElement`.

**Webpack entry points** (output to `dist/`):
- `index.js` → `app.min.js` — main application shell (login + routing)
- `aio.js` → `aio.min.js` — lazy-loaded module panels (invoice, accounting, laboral, etc.)
- `paturpat.js` → `paturpat.min.js` — PaturPat food production sub-app

**Key directories:**
- `components/` — Reusable UI primitives (`aon-input`, `aon-button`, `aon-table`, etc.)
- `modules/` — Business feature modules, each in a subdirectory (invoice, registry, accounting, laboral, messenger, etc.)
- `services/` — API calls and utilities; `service.js` is the barrel re-export for all services
- `environments/` — Constants and enums:
  - `environments.js` — re-exports all env modules (CONSTANT, EVENT, MSG, TAG, COLORS, etc.)
  - `aonTag.js` — custom element tag name constants
  - `aonApi.js` — REST endpoint path constants (all under `ms/api`)
  - `aonEvent.js` — custom DOM event name constants
  - `msg-*.js` — i18n string maps (es, en, de, fr, cat, eus, gal)
- `models/` — Plain JS data model classes

**Webpack aliases** (importable as shortcuts):
- `aoncss` → `src/main/webapp/css/aon.css`
- `aonparent` → `src/main/webapp/modules/aon-parent.js`
- `aio` → `src/main/webapp/`

### Component Pattern

Every Web Component follows this lifecycle:
```js
connectedCallback() {
    this.initialize();          // set IDs, defaults
    this.buildDur().then(() =>  // fetch DomainUserRoles (permissions)
        this.build()            // render DOM
    );
}
```

Session data is passed with every API request as HTTP headers: `session_id`, `domain_id`, `domain_name`, `domain_login` — sourced from `localStorageService.js`.

### Backend Structure (`src/main/java/com/code/aon/aio/`)

- `servlet/` — Servlet-based endpoints (login, domain management, file serving)
- `controller/` — Dashboard data controllers (DashboardController, AppController, etc.)

The backend delegates all business logic to `aon-occam` (via `aon-api` dependency). This WAR is primarily a thin servlet layer + static asset host.

### API Communication

All REST calls target the relative path `ms/api` (proxied at the server level). The `request.js` service handles XHR with AON session headers. Domain-scoped requests use `getDefaultSessionData()` which reads from localStorage.
