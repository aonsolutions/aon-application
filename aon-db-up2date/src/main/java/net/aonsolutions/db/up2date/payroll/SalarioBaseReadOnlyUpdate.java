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

public class SalarioBaseReadOnlyUpdate implements Update {

	public static final SalarioBaseReadOnlyUpdate SALARIOBASEREADONLYUPDATE = new SalarioBaseReadOnlyUpdate();
	
	private SalarioBaseReadOnlyUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		List<Integer> salarioBaseConceptIds = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("SALARIO_BASE"))
		.fetch(PAYMENT_CONCEPT.ID);
		
		dslContext.transaction( (config) -> {
			
			
			dslContext
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.EXPRESSION, DSL.castNull(CONTRACT_PAYMENT.EXPRESSION))  
			.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.in(salarioBaseConceptIds))
			.and(CONTRACT_PAYMENT.EXPRESSION.eq(DSL
					.select(PAYMENT_CONCEPT.EXPRESSION)
					.from(PAYMENT_CONCEPT)
					.where(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))))
			.execute()
			;
			
			dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.EXPRESSION, DSL.castNull(CONTRACT_PAYMENT.EXPRESSION))  
			.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.in(salarioBaseConceptIds))
			.and(AGREEMENT_PAYMENT.EXPRESSION.eq(DSL
					.select(PAYMENT_CONCEPT.EXPRESSION)
					.from(PAYMENT_CONCEPT)
					.where(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))))
			.execute()
			;

			dslContext
			.update(PAYMENT_CONCEPT)
//			.set(PAYMENT_CONCEPT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", PAYMENT_CONCEPT.EXPRESSION), "/**/") )
			.set(PAYMENT_CONCEPT.EXPRESSION, DSL.replace(PAYMENT_CONCEPT.EXPRESSION, "/*user*/", "/*read-only*/") )
			.where(PAYMENT_CONCEPT.ID.in(salarioBaseConceptIds))
			.execute()
			;

			
			
		});
	}

}
