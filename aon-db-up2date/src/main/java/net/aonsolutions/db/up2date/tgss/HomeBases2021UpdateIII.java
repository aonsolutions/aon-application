package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class HomeBases2021UpdateIII implements Update {

	private static final String TABLE = "($ in [ "
			+"[259.00,222.00], "
			+"[403.00,365.00], "
			+"[548.00,509.00], "
			+"[692.00,653.00], "
			+"[838.00,798.00], "
			+"[981.00,941.00], "
			+"[1125.90,1125.90], "
			+"[1228.00,1177.00], "
			+"[1388.00,1322.00], "
			+"[Double.MAX_VALUE,BASE_CGC_BRUTA] ] if $[0] >= BASE_CGC_BRUTA )[0][1]";
	
	public static HomeBases2021UpdateIII HOMEBASES2021UPDATEIII = new HomeBases2021UpdateIII();

	private static final int DOMAIN = -106;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MAX = "BASE_CGC_MAX";
	private static final String BASE_CGP_MAX = "BASE_CGP_MAX";
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Esablish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		
		Date startSeptember2021Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN"))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.YEAR, 2021);
		Date endAugust2021Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		Date start2019Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2019BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, endAugust2021Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2019Date))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insertSeptember2021Bases = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startSeptember2021Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE
		)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startSeptember2021Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startSeptember2021Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MAX )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startSeptember2021Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		;
		
		

		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2019BasesMin.execute();
			
			insertSeptember2021Bases.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
