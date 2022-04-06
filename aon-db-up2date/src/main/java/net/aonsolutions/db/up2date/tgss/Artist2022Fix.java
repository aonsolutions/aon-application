package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertValuesStep7;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Artist2022Fix implements Update {
	
	public static Artist2022Fix ARTIST2022FIX = new Artist2022Fix();

	private Artist2022Fix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// START_DATE 01/01/2022
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date start2022Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> update2022Artist = 
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION, "38.89.00", "38.89"))
		.where(SYSTEM_DATA.DOMAIN.eq(-108))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN"))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		;
		
		dslContext.transaction( (config) -> {
			update2022Artist.execute();
		});
	}

}
