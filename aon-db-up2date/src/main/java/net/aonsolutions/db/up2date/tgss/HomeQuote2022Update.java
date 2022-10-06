package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

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

import com.esferalia.aon.jooq.tables.SystemPayment;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class HomeQuote2022Update implements Update {

	
	public static HomeQuote2022Update HOMEQUOTE2022UPDATE = new HomeQuote2022Update();

	private static final int DOMAIN = -106;
	private static final String PORCENTAJE_FOGASA = "PORCENTAJE_FOGASA";
	private static final String PORCENTAJE_DESMPL = "PORCENTAJE_DESMPL";
	private static final String PORCENTAJE_DESMPL_E = "PORCENTAJE_DESMPL_E";
	
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
		calendar.set(Calendar.MONTH, Calendar.OCTOBER);
		calendar.set(Calendar.YEAR, 2022);
		
		Date startOctober2022Date = new Date(calendar.getTimeInMillis());
		
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		Date endNovember2022Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.START_DATE.eq(startOctober2022Date))) > 0;

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);
		
		if ( upgraded ) 
			return;



		UpdateConditionStep<SystemDataRecord> closeOldPercentages =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, endNovember2022Date)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.in(PORCENTAJE_DESMPL, PORCENTAJE_DESMPL_E, PORCENTAJE_FOGASA))
		.and(SYSTEM_DATA.END_DATE.isNull().or(SYSTEM_DATA.END_DATE.gt(endNovember2022Date)))
		;
		
		InsertOnDuplicateStep<SystemDataRecord> insertNewPercentages = 
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startOctober2022Date))
		.set(SYSTEM_DATA.EXPRESSION, "1.05")
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL_E )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startOctober2022Date))
		.set(SYSTEM_DATA.EXPRESSION, "5.00")
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, DOMAIN)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_FOGASA )
		.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_DATA.START_DATE, DSL.date(startOctober2022Date))
		.set(SYSTEM_DATA.EXPRESSION, "0.2")
		;
		
		InsertOnDuplicateStep<SystemPaymentRecord> insertNewBonus = 
		dslContext
		.insertInto(SYSTEM_PAYMENT)
		.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN)
		.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0)
		.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_PAYMENT.START_DATE, DSL.date(startOctober2022Date))
		.set(SYSTEM_PAYMENT.EXPRESSION, "SELF.addBonus('cgc_bonus', 'Reducción Contingencias Comunes (20%)', 'CGC_E * 20.00 / 100.00');HIDE()")
		.newRecord()
		.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN)
		.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0)
		.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
		.set(SYSTEM_PAYMENT.START_DATE, DSL.date(startOctober2022Date))
		.set(SYSTEM_PAYMENT.EXPRESSION, "SELF.addBonus('desmpl_bonus', 'Bonificación FOGASA y Desempleo (80%)', '(DESMPL_E + FOGASA_E) * 80.00 / 100.00');HIDE()")
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			closeOldPercentages.execute();
			
			insertNewBonus.execute();
			insertNewPercentages.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
