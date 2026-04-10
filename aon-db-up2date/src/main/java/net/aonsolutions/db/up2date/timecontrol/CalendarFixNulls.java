package net.aonsolutions.db.up2date.timecontrol;

import static com.esferalia.aon.jooq.tables.Calendar.CALENDAR;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.TableField;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.CalendarRecord;

import net.aonsolutions.db.up2date.Update;

public class CalendarFixNulls implements Update {

    public static CalendarFixNulls CALENDAR_FIX_NULLS = new CalendarFixNulls();

    private CalendarFixNulls() {}

    @Override
    public void upgrade(Connection connection) {

        Settings settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		//			PRIMITIVES
		//
		//			private double anualHours;
		//			private double annualPersonalDays;
		//			private double annualHolidays;
		//			private double mondayHours;
		//			private double tuesdayHours;
		//			private double wednesdayHours;
		//			private double thursdayHours;
		//			private double fridayHours;
		//			private double saturdayHours;
		//			private double sundayHours;
        
        List<TableField<CalendarRecord, Double>> fields = new ArrayList<>();
        fields.add(CALENDAR.ANUAL_HOURS);
        fields.add(CALENDAR.ANNUAL_PERSONAL_DAYS);
        fields.add(CALENDAR.ANNUAL_HOLIDAYS);
        fields.add(CALENDAR.MONDAY_HOURS);
        fields.add(CALENDAR.TUESDAY_HOURS);
        fields.add(CALENDAR.WEDNESDAY_HOURS);
        fields.add(CALENDAR.THURSDAY_HOURS);
        fields.add(CALENDAR.FRIDAY_HOURS);
        fields.add(CALENDAR.SATURDAY_HOURS);
        fields.add(CALENDAR.SUNDAY_HOURS);

		dslContext.transaction( config -> {
    		for ( TableField<CalendarRecord, Double> field :  fields ) {
    			dslContext.update(CALENDAR).set(field, 0.00).where(field.isNull()).execute();
    		}
		});
    }
}

