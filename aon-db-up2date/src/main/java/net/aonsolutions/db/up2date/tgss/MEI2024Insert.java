package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class MEI2024Insert implements Update {
	
	private static final String PORCENTAJE_MEI_E = "PORCENTAJE_MEI_E";

	private static final String PORCENTAJE_MEI = "PORCENTAJE_MEI";

	public static final MEI2024Insert MEI2024INSERT = new MEI2024Insert();

	private static final String MEI = "MEI";
	private static final String MEI_E = "MEI_E";

	private MEI2024Insert() {
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
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// START_DATE 01/01/2024
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2024);
		
		Date startOf2024Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.YEAR, 2023);
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date endOf2023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.YEAR, 2023);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		Date startOf2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2024Date))
			.and(SYSTEM_DATA.NAME.in(PORCENTAJE_MEI, PORCENTAJE_MEI_E))
			) >= 1;

		// IF ALREADY EXISTS
		if ( upgraded ) 
			return;
		
		
		
		dslContext.transaction( config -> {
			
			// DISABLED FOREING_KEY FOR INSERT
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// CLOSE 2023 
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, endOf2023Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2023Date))
			.and(SYSTEM_DATA.NAME.in(PORCENTAJE_MEI, PORCENTAJE_MEI_E))
			;
			
			// INSERT 2024
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_MEI)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2024Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.12" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, PORCENTAJE_MEI_E)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2024Date)
			.set(SYSTEM_DATA.EXPRESSION, "0.58" )
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
