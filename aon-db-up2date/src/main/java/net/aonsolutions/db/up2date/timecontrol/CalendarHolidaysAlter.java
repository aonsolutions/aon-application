package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CalendarHolidaysAlter implements Update {

    public static CalendarHolidaysAlter CALENDAR_HOLIDAYS_ALTER = new CalendarHolidaysAlter();

    private CalendarHolidaysAlter() {}

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
            + "  ADD COLUMN `annual_holidays` decimal(15,2) DEFAULT 0.00 COMMENT 'Vacaciones anuales' AFTER `anual_hours`,"
            + "  ADD COLUMN `holidays_type` tinyint(1) DEFAULT 0 COMMENT 'Tipo de vacaciones (naturales o laborables)' AFTER `annual_holidays`"
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

