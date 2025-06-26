package net.aonsolutions.db.up2date.finance;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class NordigenCallsLog implements Update {

    public static final NordigenCallsLog NORDIGEN_CALL_LOG_TABLE = new NordigenCallsLog();

    @Override
    public void upgrade(Connection conn) {
        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);
        DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);

        System.out.println("[START]");
        System.out.println("Creation table `nordigen_call_log`");

        String database = dslContext.fetchOne("SELECT DATABASE()").getValue(0, String.class);
        boolean existsTable = dslContext.fetchExists(
            dslContext.selectOne()
                      .from("information_schema.tables")
                      .where(DSL.field("table_name").eq(DSL.inline("nordigen_call_log")))
                      .and(DSL.field("table_schema").eq(DSL.inline(database)))
        );

        if (!existsTable) {
            String createTable =
                "CREATE TABLE IF NOT EXISTS nordigen_call_log ("
              + "    id INT NOT NULL AUTO_INCREMENT,"
              + "    domain INT NOT NULL COMMENT 'Identificador del dominio',"
              + "    rbank INT NOT NULL COMMENT 'Referencia a la cuenta bancaria',"
              + "    call_type VARCHAR(50) NOT NULL COMMENT 'Tipo de llamada: balance, transaction, etc.',"
              + "    call_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha y hora de la llamada',"
              + "    was_rate_limited BOOLEAN DEFAULT FALSE COMMENT 'Indica si se alcanzó el límite de llamadas',"
              + "    retry_after DATETIME NULL COMMENT 'Cuándo se puede volver a intentar en caso de rate limit',"
              + "    retry_count INT DEFAULT 4 COMMENT 'Número de intentos restantes en el día',"
              + "	 PRIMARY KEY (`id`)"
              + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci "
              + "COMMENT='Historial de llamadas a Nordigen para controlar el rate limit.';";

            dslContext.execute(createTable);
        }

        System.out.println("[END]");
    }
}

