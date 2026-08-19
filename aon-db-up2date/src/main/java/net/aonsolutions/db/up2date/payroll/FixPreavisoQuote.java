package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FixPreavisoQuote implements Update {

	public static final FixPreavisoQuote FIXPREAVISOQUOTE = new FixPreavisoQuote();
	
	private FixPreavisoQuote() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		
		dslContext.transaction( (config) -> {
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "FALTA PREAVISO")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.in("PREAVISO"))
			.execute()
			;

			dslContext
			.update(SYSTEM_PAYMENT.join(PAYMENT_CONCEPT).on(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID)))
			.set(SYSTEM_PAYMENT.DESCRIPTION, "FALTA PREAVISO (@{DIAS_PREAVISO} DÍAS)")
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_PREAVISO * ( SALARIO_DIA + SALARIO_VARIABLE_DIA )/**/")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.in("PREAVISO"))
			.execute()
			;
		});
	}

}
