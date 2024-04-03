package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class BasesMin2024UpdateIV implements Update {

	public static BasesMin2024UpdateIV BASESMIN2024UPDATEIV = new BasesMin2024UpdateIV();

	private static final int DOMAIN = 0;
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	private static final String BASE_CGC_MIN_HOUR = "BASE_CGC_MIN_HORA";
	private static final String PROVISIONAL_BASES = "BASES_PROVISONALES";
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
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
		calendar.set(Calendar.YEAR, 2024);
		Date start2024Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MIN_HOUR))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date))
		.and(SYSTEM_DATA.EXPRESSION.contains(PROVISIONAL_BASES))) == 0;

		if ( upgraded ) 
			return;		
		
		
		
		UpdateConditionStep<SystemDataRecord> update2024BasesMin = 
		dslContext.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, 
		DSL.regexpReplaceAll(SYSTEM_DATA.EXPRESSION , 
		"\\(\\s*BASES_PROVISONALES\\s*\\?\\s*([\\d\\.]+)\\s*:\\s*[\\d\\.]+\\s*\\)", "$1"))
		.where(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN, BASE_CGC_MIN_HOUR))
		.and(SYSTEM_DATA.START_DATE.eq(start2024Date));
		
		// DOMAIN = 0 , GENERAL
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			update2024BasesMin.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
