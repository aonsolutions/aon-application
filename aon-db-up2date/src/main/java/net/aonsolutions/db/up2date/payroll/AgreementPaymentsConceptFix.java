package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class AgreementPaymentsConceptFix implements Update {
	
	public static final AgreementPaymentsConceptFix AGREEMENTPAYMENTSCONCEPTFIX = new AgreementPaymentsConceptFix();
	

	private AgreementPaymentsConceptFix() {
	}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.transaction( (config) -> {
			
			List<Integer> paymentConceptsIds =
			config.dsl()
			.select()
			.from(AGREEMENT_PAYMENT)
			.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.notIn(DSL.select(PAYMENT_CONCEPT.ID).from(PAYMENT_CONCEPT)))
			.groupBy(AGREEMENT_PAYMENT.PAYMENT_CONCEPT)
			.fetch(AGREEMENT_PAYMENT.PAYMENT_CONCEPT);

			config.dsl()
			.insertInto(PAYMENT_CONCEPT)
			.columns(
					PAYMENT_CONCEPT.DOMAIN, 
					PAYMENT_CONCEPT.ID, 
					PAYMENT_CONCEPT.TYPE, 
					PAYMENT_CONCEPT.CODE, 
					PAYMENT_CONCEPT.DESCRIPTION, 
					PAYMENT_CONCEPT.EXPRESSION, 
					PAYMENT_CONCEPT.IRPF_EXPRESSION, 
					PAYMENT_CONCEPT.QUOTE_EXPRESSION
			)
			.select(
				DSL.select(
					AGREEMENT_PAYMENT.DOMAIN,
					AGREEMENT_PAYMENT.PAYMENT_CONCEPT,
	
					AGREEMENT_PAYMENT.TYPE,
					PAYMENT_CONCEPT.CODE,
					AGREEMENT_PAYMENT.DESCRIPTION,
					AGREEMENT_PAYMENT.EXPRESSION,
					AGREEMENT_PAYMENT.IRPF_EXPRESSION,
					AGREEMENT_PAYMENT.QUOTE_EXPRESSION
				)
				.from(AGREEMENT_PAYMENT)
				.innerJoin(PAYMENT_CONCEPT)
				.on(AGREEMENT_PAYMENT.DESCRIPTION.eq(PAYMENT_CONCEPT.DESCRIPTION))
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.notIn(DSL.select(PAYMENT_CONCEPT.ID).from(PAYMENT_CONCEPT)))
				.groupBy(AGREEMENT_PAYMENT.PAYMENT_CONCEPT)
			)
			.execute()
			;
			
			config.dsl()
			.insertInto(PAYMENT_CONCEPT)
			.columns(
					PAYMENT_CONCEPT.DOMAIN, 
					PAYMENT_CONCEPT.ID, 
					PAYMENT_CONCEPT.TYPE, 
					PAYMENT_CONCEPT.CODE, 
					PAYMENT_CONCEPT.DESCRIPTION, 
					PAYMENT_CONCEPT.EXPRESSION, 
					PAYMENT_CONCEPT.IRPF_EXPRESSION, 
					PAYMENT_CONCEPT.QUOTE_EXPRESSION
			)
			.select(
				DSL.select(
					AGREEMENT_PAYMENT.DOMAIN,
					AGREEMENT_PAYMENT.PAYMENT_CONCEPT,
	
					AGREEMENT_PAYMENT.TYPE,
					DSL.val("PAGA_EXTRA", SQLDataType.VARCHAR),
					AGREEMENT_PAYMENT.DESCRIPTION,
					AGREEMENT_PAYMENT.EXPRESSION,
					AGREEMENT_PAYMENT.IRPF_EXPRESSION,
					AGREEMENT_PAYMENT.QUOTE_EXPRESSION
				)
				.from(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.notIn(DSL.select(PAYMENT_CONCEPT.ID).from(PAYMENT_CONCEPT)))
				.and(AGREEMENT_PAYMENT.DESCRIPTION.like("%PAGA%"))
				.groupBy(AGREEMENT_PAYMENT.PAYMENT_CONCEPT)
			)
			.execute()
			;

			config.dsl()
			.insertInto(PAYMENT_CONCEPT)
			.columns(
					PAYMENT_CONCEPT.DOMAIN, 
					PAYMENT_CONCEPT.ID, 
					PAYMENT_CONCEPT.TYPE, 
					PAYMENT_CONCEPT.CODE, 
					PAYMENT_CONCEPT.DESCRIPTION, 
					PAYMENT_CONCEPT.EXPRESSION, 
					PAYMENT_CONCEPT.IRPF_EXPRESSION, 
					PAYMENT_CONCEPT.QUOTE_EXPRESSION
			)
			.select(
				DSL.select(
					AGREEMENT_PAYMENT.DOMAIN,
					AGREEMENT_PAYMENT.PAYMENT_CONCEPT,
	
					AGREEMENT_PAYMENT.TYPE,
					DSL.replace(DSL.substringIndex(DSL.substring(AGREEMENT_PAYMENT.DESCRIPTION, 6), " ", 2), " ", "_"),
					AGREEMENT_PAYMENT.DESCRIPTION,
					AGREEMENT_PAYMENT.EXPRESSION,
					AGREEMENT_PAYMENT.IRPF_EXPRESSION,
					AGREEMENT_PAYMENT.QUOTE_EXPRESSION
				)
				.from(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.notIn(DSL.select(PAYMENT_CONCEPT.ID).from(PAYMENT_CONCEPT)))
				.and(AGREEMENT_PAYMENT.DESCRIPTION.likeRegex("\\[\\d{2}\\] .*"))
				.groupBy(AGREEMENT_PAYMENT.PAYMENT_CONCEPT)
			)
			.execute()
			;

			config.dsl()
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.ID.in(paymentConceptsIds))
			.fetch()
			.format(System.out)
			;

		});
	}

	
}
