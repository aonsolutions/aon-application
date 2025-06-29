package net.aonsolutions.db.up2date.registry;

import java.sql.Connection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AddRegistryBankAgreement implements Update {
    
    public static final AddRegistryBankAgreement ADD_AGREEMENT_COLUMN = new AddRegistryBankAgreement();

    @Override
    public void upgrade(Connection conn) {
        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);
        DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);

        System.out.println("[START]");
        System.out.println("Alter table `rbank` to add columns `agreement` and `days_until_agreement_ends`");

        String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);

        // Añadir columna agreement si no existe
        boolean agreementColumnExists = dslContext.fetchExists(
            dslContext.selectOne()
                      .from("information_schema.columns")
                      .where(DSL.field("table_name").eq(DSL.inline("rbank")))
                      .and(DSL.field("table_schema").eq(DSL.inline(database)))
                      .and(DSL.field("column_name").eq(DSL.inline("agreement")))
        );

        if (!agreementColumnExists) {
            String alterTable = "ALTER TABLE rbank ADD COLUMN agreement VARCHAR(255) NULL COMMENT 'Id acuerdo asociado a la cuenta bancaria'";
            dslContext.execute(alterTable);
            System.out.println("Columna `agreement` añadida correctamente.");
        } else {
            System.out.println("La columna `agreement` ya existe, no se realiza ningún cambio.");
        }

        boolean daysColumnExists = dslContext.fetchExists(
            dslContext.selectOne()
                      .from("information_schema.columns")
                      .where(DSL.field("table_name").eq(DSL.inline("rbank")))
                      .and(DSL.field("table_schema").eq(DSL.inline(database)))
                      .and(DSL.field("column_name").eq(DSL.inline("days_until_agreement_ends")))
        );

        if (!daysColumnExists) {
            String alterTable = "ALTER TABLE rbank ADD COLUMN days_until_agreement_ends INT NULL COMMENT 'Días hasta la finalización del acuerdo'";
            dslContext.execute(alterTable);
            System.out.println("Columna `days_until_agreement_ends` añadida correctamente.");
        } else {
            System.out.println("La columna `days_until_agreement_ends` ya existe, no se realiza ningún cambio.");
        }

        System.out.println("[END]");
    }
}

