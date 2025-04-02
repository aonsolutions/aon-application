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

public class AgriculturalPercentageCgcE2025Fix implements Update {

	public static AgriculturalPercentageCgcE2025Fix AGRICULTURALPERCENTAGECGCE2025FIX = new AgriculturalPercentageCgcE2025Fix();

	private static final int DOMAIN = -107;
	private static final String PORCENTAJE_CGC_E = "PORCENTAJE_CGC_E";
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC_E))
		.and(SYSTEM_DATA.END_DATE.isNull())) == 0;

		if ( upgraded ) 
			return;		
		
		

		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.YEAR, 2024);
			Date end2024Date = new Date(calendar.getTimeInMillis());
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, end2024Date)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC_E))
			.and(SYSTEM_DATA.END_DATE.isNull())
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
