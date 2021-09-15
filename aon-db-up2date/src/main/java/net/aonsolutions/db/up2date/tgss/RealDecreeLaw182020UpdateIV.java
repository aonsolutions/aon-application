package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RealDecreeLaw182020UpdateIV implements Update {

	public static final RealDecreeLaw182020UpdateIV REALDECREELAW182020UPDATEIV = new RealDecreeLaw182020UpdateIV();
	private static final String PORCENTAJE_EXONERADO = "PORCENTAJE_EXONERADO";
	
	private RealDecreeLaw182020UpdateIV() {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.YEAR, 2020);		

		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		LocalDate july1Date = new Date(calendar.getTimeInMillis()).toLocalDate();
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		LocalDate july31Date = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		LocalDate august1Date = new Date(calendar.getTimeInMillis()).toLocalDate();
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		LocalDate august31Date = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		LocalDate september1Date = new Date(calendar.getTimeInMillis()).toLocalDate();
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		LocalDate september30Date = new Date(calendar.getTimeInMillis()).toLocalDate();

		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_EXONERADO))
			.and(SYSTEM_DATA.START_DATE.ge(july1Date))
			.execute();
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_REINCORPORACION"))
			.and(SYSTEM_DATA.START_DATE.ge(july1Date))
			.execute();

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_EXONERADO)
			.set(SYSTEM_DATA.END_DATE, july31Date)
			.set(SYSTEM_DATA.START_DATE, july1Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 70.00 : 35.00 ")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_EXONERADO)
			.set(SYSTEM_DATA.END_DATE, august31Date)
			.set(SYSTEM_DATA.START_DATE, august1Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 60.00 : 35.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_EXONERADO)
			.set(SYSTEM_DATA.END_DATE, september30Date)
			.set(SYSTEM_DATA.START_DATE, september1Date)
			.set(SYSTEM_DATA.EXPRESSION,"35.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_REINCORPORACION")
			.set(SYSTEM_DATA.END_DATE, september30Date)
			.set(SYSTEM_DATA.START_DATE, july1Date)
			.set(SYSTEM_DATA.EXPRESSION,"60.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
