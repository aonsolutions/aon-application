package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class Remove2HideUpdate implements Update {

	public static final Remove2HideUpdate REMOVE2HIDEUPDATE = new Remove2HideUpdate();
	
	private Remove2HideUpdate() {
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
			
			List<Integer> indemnizaciones = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte) 54))
			.fetch(PAYMENT_CONCEPT.ID);

			int updated = dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "REMOVE","HIDE" ))
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizaciones))
			.execute()
			;
			
			System.out.print("Hide " + updated + " 'INDEMNIZACIONES...' payments. ");
			
			List<Integer> mejoras = 
			dslContext
			.select(PAYMENT_CONCEPT.ID)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.in((byte) 55, (byte) 56))
			.fetch(PAYMENT_CONCEPT.ID);
			
			updated = dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.replace(AGREEMENT_PAYMENT.EXPRESSION, "REMOVE","HIDE" ))
			.where( AGREEMENT_PAYMENT.PAYMENT_CONCEPT.in(mejoras ))
			.execute()
			;
			
			System.out.print("Hide " + updated + " 'MEJORAS...' payments.");
			
		});
	}

}
