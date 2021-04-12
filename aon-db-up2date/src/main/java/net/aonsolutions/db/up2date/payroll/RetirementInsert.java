package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.Optional;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RetirementInsert implements Update {

	public static final RetirementInsert RETIREMENTINSERT = new RetirementInsert();

	private static final String RETIREMENT_DESCRIPTIOIN = "INDEMNIZACI\u00D3N POR JUBILACI\u00D3N DEL EMPRESARIO";
	private static final String RETIREMENT_EXPRESSION = "(CAUSA_INDEMNIZACION == JUBILACION) ?/*user*/ SALARIO_MES /**/: __HIDE_";

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Integer indemnizacionConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("INDEMNIZACION"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(indemnizacionConceptId))
		.and(SYSTEM_PAYMENT.EXPRESSION.eq(RETIREMENT_EXPRESSION))
		) >= 1;		
		
		if ( upgraded )
			return;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		Date _2010StartDate = new Date(calendar.getTimeInMillis());
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 2)
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, indemnizacionConceptId)
			.set(SYSTEM_PAYMENT.EXPRESSION, RETIREMENT_EXPRESSION)
			.set(SYSTEM_PAYMENT.DESCRIPTION, RETIREMENT_DESCRIPTIOIN)
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
