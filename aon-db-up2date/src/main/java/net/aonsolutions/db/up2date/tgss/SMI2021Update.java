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

import com.esferalia.aon.jooq.tables.SystemDeduction;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class SMI2021Update implements Update {

	public static SMI2021Update SMI2020UPDATE = new SMI2021Update();

	private static final String SMI = "SMI";
	
	private SMI2021Update() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date _2021SeptemberStartDate = new Date(calendar.getTimeInMillis());

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date _2021AugustEndDate = new Date(calendar.getTimeInMillis());


		calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2020);
		Date _2020StartDate = new Date(calendar.getTimeInMillis());


		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2021SeptemberStartDate))) > 0;
		


		if ( upgraded ) 
			return;
		
		;
		// SMI
		UpdateConditionStep<SystemDataRecord> updateSMI = dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, _2021AugustEndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2020StartDate));
		;
		
		InsertSetMoreStep<SystemDataRecord> insertSMI = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, SMI)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2021SeptemberStartDate)
		.set(SYSTEM_DATA.EXPRESSION, "965.00" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateSMI.execute();
			insertSMI.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
