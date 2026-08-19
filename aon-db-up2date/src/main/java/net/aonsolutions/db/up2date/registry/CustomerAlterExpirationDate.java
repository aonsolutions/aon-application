package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;
import java.util.function.Predicate;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

/**
 * Anade la columna {@code customer.expiration_date} justo despues de
 * {@code customer.status}.
 *
 * Semantica de la columna (equivalente a {@code domain.expirationDate}):
 *
 * <pre>
 *  status    | expiration_date        | domain.active | domain.expirationDate
 *  ----------+------------------------+---------------+----------------------
 *  ACTIVE    | NULL                   | 1             | NULL
 *  INACTIVE  | NULL                   | 0             | NULL
 *  BLOCKED   | fecha (hoy si vacia)   | 1             | misma fecha
 * </pre>
 *
 * Invariantes:
 * <ul>
 *   <li>status = BLOCKED  =&gt; expiration_date NOT NULL</li>
 *   <li>expiration_date NOT NULL =&gt; status = BLOCKED</li>
 * </ul>
 *
 * Un cliente BLOCKED cuya fecha de expiracion aun no ha llegado se considera
 * ACTIVO a efectos de filtrado.
 */
public class CustomerAlterExpirationDate implements Update {

    public static final CustomerAlterExpirationDate CUSTOMERALTEREXPIRATIONDATE = new CustomerAlterExpirationDate();

    private static final String TABLE = "customer";

    private CustomerAlterExpirationDate() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dsl = DSL.using(connection, SQLDialect.MYSQL, settings);

        dsl.transaction(cfg -> {

            DSLContext ctx = DSL.using(cfg);

            // -----------------------------
            // Helpers reutilizables
            // -----------------------------
            Predicate<String> columnExists = col ->
                ctx.fetchExists(
                    DSL.selectOne()
                       .from("INFORMATION_SCHEMA.COLUMNS")
                       .where(DSL.field("TABLE_NAME").eq(TABLE))
                       .and(DSL.field("TABLE_SCHEMA").eq(DSL.field("DATABASE()", String.class)))
                       .and(DSL.field("COLUMN_NAME").eq(col))
                );

            // -----------------------------
            // Columnas
            // -----------------------------
            addColumnIfMissing(ctx, columnExists,
                "expiration_date",
                "ALTER TABLE `customer` "
              + "ADD COLUMN `expiration_date` date DEFAULT NULL "
              + "COMMENT 'Fecha de expiracion. Solo aplica cuando status = BLOCKED' "
              + "AFTER `status`"
            );
        });
    }

    // ---------------------------------------------------------
    // Helper generico para anadir columnas si no existen
    // ---------------------------------------------------------
    private void addColumnIfMissing(DSLContext ctx,
                                    Predicate<String> existsFn,
                                    String column,
                                    String sql) {

        if (!existsFn.test(column)) {
            System.out.println("[DB] Anadiendo columna '" + column + "'...");
            ctx.execute(sql);
        } else {
            System.out.println("[DB] Columna '" + column + "' ya existe, no se modifica.");
        }
    }
}