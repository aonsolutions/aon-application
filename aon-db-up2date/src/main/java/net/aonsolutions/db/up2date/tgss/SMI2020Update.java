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

public class SMI2020Update implements Update {

	public static SMI2020Update SMI2020UPDATE = new SMI2020Update();

	private static final String SMI = "SMI";
	private static final String ROUND = "ROUND";
	private static final String BASE_CGC_MIN = "BASE_CGC_MIN";
	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";
	
	private SMI2020Update() {
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


		calendar.add(Calendar.YEAR, -1);
		Date _2019StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2019EndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("BASE_CGC_MIN_WARN"))
		.and(SYSTEM_DATA.START_DATE.eq(_2020StartDate))) > 0;
		


		if ( upgraded ) 
			return;

		// WARNING
		InsertSetMoreStep<SystemDataRecord> insertCgcMinWarningMsg = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "BASE_CGC_MIN_WARN")
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2020StartDate)
		.set(SYSTEM_DATA.EXPRESSION,  
		"\"<div>Base m&iacute;nima provisional.</div>"
		+"<div>Incrementada en el porcentaje experimentado para el año 2020 por el Salario M&iacute;nimo.</div>"
		+"<div>&nbsp;</div>"
		+"<div class='aon-text-right'>Disculpe las molestias, <span class='aon-icon aon-icon-logo' />aon Solutions</div>\""
		)
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		InsertSetMoreStep<SystemDeductionRecord> insertCgcMinWarningDeduction = 
		dslContext
		.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.DOMAIN, 0)
		.set(SYSTEM_DEDUCTION.END_DATE, (Date) null)
		.set(SYSTEM_DEDUCTION.START_DATE, _2020StartDate)
		.set(SYSTEM_DEDUCTION.EXPRESSION,
		"( BASE_CGC_BRUTA < BASE_CGC ) ? HIDE(BASE_CGC_MIN_WARN) : HIDE();"
		)
		
		;
		// SMI
		UpdateConditionStep<SystemDataRecord> updateSMI = dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, _2019EndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(SMI))
		.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
		;
		
		InsertSetMoreStep<SystemDataRecord> insertSMI = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, SMI)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2020StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "950.00" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		
		UpdateConditionStep<SystemDataRecord> updateBasesMinMonth = dslContext
				.update(SYSTEM_DATA)
				.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION, "1050.00", String.format("%s(%s*14/12,2)", ROUND, SMI) ))
				.where(SYSTEM_DATA.DOMAIN.eq(0))
				.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
				.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
				;

		UpdateConditionStep<SystemDataRecord> updateBasesMinDay = dslContext
				.update(SYSTEM_DATA)
				.set(SYSTEM_DATA.EXPRESSION, DSL.replace(SYSTEM_DATA.EXPRESSION, "35.00", String.format("%s((%s*14/12)/30,2)", ROUND, SMI) ))
				.where(SYSTEM_DATA.DOMAIN.eq(0))
				.and(SYSTEM_DATA.NAME.in(BASE_CGC_MIN, BASE_CGP_MIN))
				.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate));
				;
		
		// DOMAIN = 0 , GENERAL

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateSMI.execute();
			insertSMI.execute();
			updateBasesMinDay.execute();
			updateBasesMinMonth.execute();
			insertCgcMinWarningMsg.execute();
			insertCgcMinWarningDeduction.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
