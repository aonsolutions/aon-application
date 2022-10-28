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

public class HomeBonus2012Update implements Update {

	
	public static HomeBonus2012Update HOMEBONUS2012UPDATE = new HomeBonus2012Update();

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
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2012);
		
		Date start2012Date = new Date(calendar.getTimeInMillis());
		
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.YEAR, 2022);

		Date endSeptember2022Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_PAYMENT.START_DATE.eq(start2012Date))) > 0;

		if ( upgraded ) 
			return;
		
		InsertOnDuplicateStep<SystemPaymentRecord> insertNewBonus = 
		dslContext
		.insertInto(SYSTEM_PAYMENT)
		.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN)
		.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0)
		.set(SYSTEM_PAYMENT.END_DATE, DSL.date(endSeptember2022Date))
		.set(SYSTEM_PAYMENT.START_DATE, DSL.date(start2012Date))
		.set(SYSTEM_PAYMENT.EXPRESSION, "SELF.addBonus('cgc_bonus', 'Reducción Contingencias Comunes (20%)', 'CGC_E * 20.00 / 100.00');HIDE()")
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insertNewBonus.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
