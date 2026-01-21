package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TimecontrolLocationAlter implements Update {

    public static TimecontrolLocationAlter TIMECONTROL_LOCATION_ALTER = new TimecontrolLocationAlter();

    private TimecontrolLocationAlter() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dsl = DSL.using(connection, SQLDialect.MARIADB, settings);

        System.out.println("[START]");
        System.out.println("Alter table `timecontrol`");

        String sql =
            "ALTER TABLE `timecontrol` " +
            "DROP FOREIGN KEY `FK_TIMECONTROL_LOCATION`, " +
            "ADD COLUMN `location_description` varchar(64) DEFAULT NULL " +
            "COMMENT 'Descripción de la ubicación' AFTER `location`;";

        try {
            dsl.execute(sql);
            System.out.println("[table 'timecontrol' ALTERED!]");
        } catch (Throwable t) {
            System.out.println("[table 'timecontrol' NOT ALTERED!]");
            t.printStackTrace();
        }

        System.out.println("[END]");
    }
}

