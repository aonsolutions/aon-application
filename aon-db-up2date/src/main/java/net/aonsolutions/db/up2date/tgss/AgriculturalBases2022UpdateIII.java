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

public class AgriculturalBases2022UpdateIII implements Update {

	public static AgriculturalBases2022UpdateIII AGRICULTURALBASES2021UPDATEIII = new AgriculturalBases2022UpdateIII();

	private static final int DOMAIN = -107;
	private static final String BASE_CGC_MIN_DIA = "BASE_CGC_MIN_DIA";
	
	private static final String BASE_CGP_MIN_DIA = "BASE_CGP_MIN_DIA";
	
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
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date start2022Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN_DIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
		.and(SYSTEM_DATA.EXPRESSION.eq(BASE_CGC_MIN_DIA))) > 0;

		if ( upgraded ) 
			return;


		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, BASE_CGC_MIN_DIA)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN_DIA))
			.and(SYSTEM_DATA.START_DATE.eq(start2022Date))
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
