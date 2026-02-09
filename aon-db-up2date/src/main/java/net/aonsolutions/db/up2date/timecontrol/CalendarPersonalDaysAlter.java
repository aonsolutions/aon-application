package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CalendarPersonalDaysAlter implements Update {

    public static CalendarPersonalDaysAlter CALENDAR_PERSONAL_DAYS_ALTER = new CalendarPersonalDaysAlter();

    private CalendarPersonalDaysAlter() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dsl = DSL.using(connection, SQLDialect.MARIADB, settings);

        System.out.println("[START]");
        System.out.println("Alter table `calendar`");

        String sql =
            "ALTER TABLE `calendar`"
            + "  ADD COLUMN `annual_personal_days` decimal(15,2) DEFAULT 0.00 COMMENT 'Dias libre disposicion' AFTER `anual_hours`"
            + ";";

        try {
            dsl.execute(sql);
            System.out.println("[table 'calendar' ALTERED!]");
        } catch (Throwable t) {
            System.out.println("[table 'calendar' NOT ALTERED!]");
            t.printStackTrace();
        }

        System.out.println("[END]");
    }
}

