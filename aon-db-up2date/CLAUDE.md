# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

`aon-db-up2date` is a database migration tool for the AON ERP system. It connects to a MySQL server, discovers all databases containing a `registry` table, and sequentially applies a list of `Update` implementations to each database.

## Build Commands

```bash
# Build fat JAR (includes all dependencies)
mvn package

# Build without running tests
mvn package -DskipTests

# The output artifact is:
# target/aon.db.up2date-9.23-SNAPSHOT-jar-with-dependencies.jar
```

## Running

```bash
# Run directly
java -jar target/aon.db.up2date-9.23-SNAPSHOT-jar-with-dependencies.jar \
  --host=localhost --port=3306 --user=aonsolutions --password=40ns0lut10ns

# Via Docker (env vars with defaults shown)
docker run \
  -e DB_HOST=localhost \
  -e DB_PORT=3306 \
  -e DB_USER=aonsolutions \
  -e DB_PASSWD=40ns0lut10ns \
  aon-db-up2date
```

## Architecture

### Core Pattern

The tool uses a static registry pattern in `Up2Date.java`:

1. `Update` — a single-method interface: `void upgrade(Connection conn)`
2. `Up2Date.UPDATES` — a static array of `Update` singletons to execute
3. `Up2Date.main()` — connects to MySQL, finds all databases with a `registry` table, then calls `upgrade(connection)` for each
4. Updates run sequentially; each error is caught and printed but does not stop subsequent updates

### Adding a New Update

1. Create a class in the appropriate domain package (e.g., `net.aonsolutions.db.up2date.payroll`) implementing `Update`
2. Use the singleton pattern with a `public static final` instance
3. Use JOOQ with `Settings` configured for `SQLDialect.MYSQL` and `ParamType.INLINED`, `setRenderSchema(false)`
4. Wrap operations in `dslContext.transaction(config -> { ... })`
5. Register the instance in the `UPDATES` array in `Up2Date.java`

Typical update skeleton:
```java
public class MyUpdate implements Update {
    public static final MyUpdate MY_UPDATE = new MyUpdate();
    private MyUpdate() {}

    @Override
    public void upgrade(Connection connection) {
        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);
        DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
        dslContext.transaction(config -> {
            // JOOQ operations using generated tables from aon.master.jooq
        });
    }
}
```

### Key Dependency: `aon.master.jooq`

JOOQ-generated table/record classes come from the sibling module `com.esferalia.aon:aon.master.jooq`. Import table constants statically, e.g.:
```java
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
```

### Domain Packages

Updates are grouped by functional domain under `src/main/java/net/aonsolutions/db/up2date/`:

| Package | Domain |
|---|---|
| `payroll` | Salary, payment concepts, deductions, benefits |
| `tgss` | Social Security regulations |
| `fiscal` | Tax models (190, 193, 200, 303, 347, 349, 369) |
| `irpf` | Income tax (IRPF) |
| `finance` | Invoices, bank statements, accounting |
| `security` | Permissions, auth, access control |
| `marketing` | Campaigns, actions, budgets |
| `customize` | Domain-specific branding (theme, logos, app params) |
| `registry` | Registry and bank data |
| `timecontrol` | Time tracking |
| `task` | Task workflows and evaluations |

### Update Lifecycle in `UPDATES` Array

Completed/old updates are commented out but kept for historical reference. Only active updates (currently just `DeleteUnusedPaymentConcepts`) are uncommented. When enabling new updates, uncomment them in `Up2Date.UPDATES`; when retiring them after all databases are migrated, comment them back out.

The comment `// Warning. Don't delete or comment following instructions` marks permanently active updates that must always run.
