package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class HomePercentageUpdate implements Update {

	private static final String PORCENTAJE_CGC = "PORCENTAJE_CGC";
	private static final String PORCENTAJE_CGC_E = "PORCENTAJE_CGC_E";

	public static final HomePercentageUpdate HOMEPERCENTAGEUPDATE = new HomePercentageUpdate();
	
	private HomePercentageUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2018);
		
		Date _2018StartDate = new Date(calendar.getTimeInMillis());


		calendar.add(Calendar.YEAR, -1);
		Date _2017StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2017EndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded = 
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC_E))
			.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded ) 
			return;
		
		// CLEAN OLD 2018
		DeleteConditionStep<SystemDataRecord> delete2018 = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.in(PORCENTAJE_CGC, PORCENTAJE_CGC_E))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));

		// CLOSE 2017
		UpdateConditionStep<SystemDataRecord> close2017 = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.in(-106))
		.and(SYSTEM_DATA.NAME.in(PORCENTAJE_CGC, PORCENTAJE_CGC_E))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;
		
		InsertSetMoreStep<SystemDataRecord> insertCgcPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_CGC)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "4.55" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		InsertSetMoreStep<SystemDataRecord> insertCgcEPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_CGC_E)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "22.85" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			delete2018.execute();
			close2017.execute();
			insertCgcPercentage.execute();
			insertCgcEPercentage.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
