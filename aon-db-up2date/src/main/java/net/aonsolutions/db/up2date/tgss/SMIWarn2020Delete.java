package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class SMIWarn2020Delete implements Update {

	public static SMIWarn2020Delete SMIWARN2020DELETE = new SMIWarn2020Delete();

	private SMIWarn2020Delete() {
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
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2020);
		
		Date _2020StartDate = new Date(calendar.getTimeInMillis());


		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_WARN"))
		.and(SYSTEM_DATA.START_DATE.eq(_2020StartDate))) == 0;

		if ( upgraded ) 
			return;
		
		// WARNING
		DeleteConditionStep<SystemDataRecord> deleteCgcMinWarningMsg = 
		dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_WARN"))
		.and(SYSTEM_DATA.START_DATE.eq(_2020StartDate));
		
		DeleteConditionStep<SystemDeductionRecord> deleteCgcMinWarningDeduction = 
		dslContext
		.delete(SYSTEM_DEDUCTION)
		.where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
		.and(SYSTEM_DEDUCTION.START_DATE.eq(_2020StartDate))
		.and(SYSTEM_DEDUCTION.EXPRESSION.eq(
		"( BASE_CGC_BRUTA < BASE_CGC ) ? HIDE(BASE_CGC_MIN_WARN) : HIDE();"
		))
		
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			deleteCgcMinWarningMsg.execute();
			deleteCgcMinWarningDeduction.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
