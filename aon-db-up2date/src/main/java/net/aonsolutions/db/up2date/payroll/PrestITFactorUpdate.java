package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class PrestITFactorUpdate implements Update {

	public static final PrestITFactorUpdate PRESTITFACTORUPDATE = new PrestITFactorUpdate();
	
	private PrestITFactorUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Integer prestItConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PREST_IT"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		
		Integer pagoDirectoConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGO_DIRECTO"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00) * BASE_REGULADORA")
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "/**/", " * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/") )
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
			.and(SYSTEM_PAYMENT.QUOTE_EXPRESSION.notContains("COEFICIENTE_IT"))
			.execute()
			;
			
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00) * BASE_REGULADORA")
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(pagoDirectoConceptId))
			.and(SYSTEM_PAYMENT.QUOTE_EXPRESSION.notContains("COEFICIENTE_IT"))
			.execute()
			;



			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
