package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.AgreementPayment;

import net.aonsolutions.db.up2date.Update;

public class PlusTransporte0001Insert implements Update {

	public static final PlusTransporte0001Insert PLUSTRANSPORTE0001INSERT = new PlusTransporte0001Insert();
	
	private PlusTransporte0001Insert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)1))
		.and(PAYMENT_CONCEPT.DESCRIPTION.eq("PLUS DE TRANSPORTE"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			//config.dsl().execute("SET FOREIGN_KEY_CHECKS=0;");
			
			List<Integer> plusTransporte0032Concepts =
			config.dsl()
			.selectFrom(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte) 32))
			.fetch(PAYMENT_CONCEPT.ID);

			Integer plusTransporte0001Concet = 
			config.dsl()
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.EXPRESSION, "0.00" )
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "PLUS DE TRANSPORTE")
			.returning().fetchOne(PAYMENT_CONCEPT.ID)
			;
			
			// Update ContractPayment
			config.dsl()
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, plusTransporte0001Concet)
			.set(CONTRACT_PAYMENT.DESCRIPTION, DSL.regexpReplaceAll(CONTRACT_PAYMENT.DESCRIPTION, "([ \\[]*0*)32([ \\]]*)", "$101$2"))
			.set(CONTRACT_PAYMENT.TYPE, DSL.if_(CONTRACT_PAYMENT.TYPE.in((byte)32, (byte)1), DSL.castNull(CONTRACT_PAYMENT.TYPE), CONTRACT_PAYMENT.TYPE))
			.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.in(plusTransporte0032Concepts))
			.execute();
			
			// Update AgreementPayment similarly
			config.dsl()
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, plusTransporte0001Concet)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, DSL.regexpReplaceAll(AGREEMENT_PAYMENT.DESCRIPTION, "([ \\[]*0*)32([ \\]]*)", "$101$2"))
			.set(AGREEMENT_PAYMENT.TYPE, DSL.if_(AGREEMENT_PAYMENT.TYPE.in((byte)32, (byte)1), DSL.castNull(AGREEMENT_PAYMENT.TYPE), AGREEMENT_PAYMENT.TYPE))
			.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.in(plusTransporte0032Concepts))
			.execute();
			
			// Finally, set type 1 to all type 32 payment concepts in all agreements
			config.dsl()
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.DESCRIPTION, DSL.regexpReplaceAll(PAYMENT_CONCEPT.DESCRIPTION, "([ \\[]*0*)32([ \\]]*)", "$101$2"))
			.where(PAYMENT_CONCEPT.DOMAIN.gt(0))
			.and(PAYMENT_CONCEPT.TYPE.eq((byte)32))
			.execute();
			

			//dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
