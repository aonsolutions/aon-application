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

public class HomeBases2024Update implements Update {

	public static final HomeBases2024Update HOMEBASES2024UPDATEIV = new HomeBases2024Update();

	private static final String TABLE = "($ in [ "
			+"[306.00,284.00], "
			+"[474.00,405.00], "
			+"[644.00,559.00], "
			+"[814.00,729.00], "
			+"[986.00,901.00], "
			+"[1153.00,1069.00], "
			+"[1323.00,1323.00], "
			+"[Double.MAX_VALUE,BASE_CGC_BRUTA / DIAS_COTIZADOS * DIAS_MES ] ] if $[0] >= ( BASE_CGC_BRUTA / DIAS_COTIZADOS * DIAS_MES ) )[0][1] / DIAS_MES * DIAS_COTIZADOS";

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

		calendar.set(Calendar.YEAR, 2024);
		Date start2024Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGC_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))) > 0;

		if ( upgraded ) 
			return;

		calendar.set(Calendar.YEAR, 2023);
		Date start2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date end2023Date = new Date(calendar.getTimeInMillis());

		UpdateConditionStep<SystemDataRecord> close2023BasesMin =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, end2023Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MAX, BASE_CGP_MAX))
		.and(SYSTEM_DATA.START_DATE.eq(start2023Date))
		;

		InsertOnDuplicateStep<SystemDataRecord> insert2024Bases = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MIN )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2024Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGC_MAX )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2024Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2024Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MAX )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(start2024Date))
		.set(SYSTEM_DATA.EXPRESSION, TABLE)
		;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			close2023BasesMin.execute();
			insert2024Bases.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
