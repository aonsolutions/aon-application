package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class CgcEMinInsert implements Update {

	private static final String CGC_E_MIN_MES = "CGC_E_MIN_MES";

	private static final String CGC_E_MIN_DIA = "CGC_E_MIN_DIA";

	public static final CgcEMinInsert CGCEMININSERT = new CgcEMinInsert();


	private CgcEMinInsert() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2026);
		Date startOf2026Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2025);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date endOf2025Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2025);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date startOf2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-107))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.and(SYSTEM_DATA.NAME.in(CGC_E_MIN_MES, CGC_E_MIN_DIA))
			) >= 1;

		// IF ALREADY EXISTS
		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			// DISABLED FOREING_KEY FOR INSERT
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			// INSERT 2025
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, -107)
			.set(SYSTEM_DATA.NAME, CGC_E_MIN_DIA)
			.set(SYSTEM_DATA.END_DATE, endOf2025Date)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "7.45" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, -107)
			.set(SYSTEM_DATA.NAME, CGC_E_MIN_MES)
			.set(SYSTEM_DATA.END_DATE, endOf2025Date)
			.set(SYSTEM_DATA.START_DATE, startOf2025Date)
			.set(SYSTEM_DATA.EXPRESSION, "163.84" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			// INSERT 2026
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, -107)
			.set(SYSTEM_DATA.NAME, CGC_E_MIN_DIA)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "8.16" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, -107)
			.set(SYSTEM_DATA.NAME, CGC_E_MIN_MES)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.EXPRESSION, "179.44" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
