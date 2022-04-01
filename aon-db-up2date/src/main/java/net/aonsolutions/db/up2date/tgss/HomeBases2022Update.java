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

public class HomeBases2022Update implements Update {

	private static final String TABLE = "($ in [ "
			+"[269.00,231.00], "
			+"[418.00,379.00], "
			+"[568.00,528.00], "
			+"[718.00,677.00], "
			+"[869.00,827.00], "
			+"[1017.00,976.00], "
			+"[1166.70,1166.70], "
			+"[1273.00,1220.00], "
			+"[1439.00,1370.00], "
			+"[Double.MAX_VALUE,BASE_CGC_BRUTA] ] if $[0] >= BASE_CGC_BRUTA )[0][1]";
	
	public static HomeBases2022Update HOMEBASES2022UPDATE = new HomeBases2022Update();

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
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2022);
		
		Date start2022Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2022Date))) > 0;

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		
		Date startSeptember2021Date = new Date(calendar.getTimeInMillis());

		if ( upgraded ) 
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date end2021Date = new Date(calendar.getTimeInMillis());


		UpdateConditionStep<SystemDataRecord> close2021BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2021Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(startSeptember2021Date))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insert2022Bases = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2022Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2022Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2022Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MAX )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2022Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2021BasesMin.execute();
			
			insert2022Bases.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
