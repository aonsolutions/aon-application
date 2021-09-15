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

public class RealDecreeLaw182020UpdateIII implements Update {

	public static final RealDecreeLaw182020UpdateIII REALDECREELAW182020UPDATEIII = new RealDecreeLaw182020UpdateIII();

	
	private RealDecreeLaw182020UpdateIII() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 13);
		calendar.set(Calendar.MONTH, Calendar.MAY);
		calendar.set(Calendar.YEAR, 2020);		
		LocalDate may13Date = new Date(calendar.getTimeInMillis()).toLocalDate();


		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 100.00 : (TRAMO(DIA(CONTEXT.getCurrentBindings().getPeriod().getStart(),-1)); 60.00 )")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.START_DATE.eq(may13Date))
			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
