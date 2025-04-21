package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.AgreementPayment;
import com.esferalia.aon.jooq.tables.ContractPayment;
import com.esferalia.aon.jooq.tables.PaymentConcept;

import net.aonsolutions.db.up2date.Update;

public class DeleteUnusedPaymentConcepts implements Update {
	
	public static final DeleteUnusedPaymentConcepts DELETEUNUSEDPAYMENTCONCEPTS = new DeleteUnusedPaymentConcepts();
	

	private DeleteUnusedPaymentConcepts() {
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
			
			// --------------------------------------------------------------------------------------------------------
			// Original SQL
			//
			//DELETE payment_concept 
			// FROM payment_concept 
			// LEFT JOIN agreement_payment ON ( payment_concept.id = agreement_payment.payment_concept)  
			// LEFT JOIN contract_payment ON ( payment_concept.id =  contract_payment.payment_concept ) 
			// WHERE payment_concept.domain >  0 AND agreement_payment.id IS NULL AND contract_payment.id IS NULL ;
			
			int deleted = 
			config.dsl()
			.deleteFrom(PAYMENT_CONCEPT)
			.using(PAYMENT_CONCEPT
					.leftJoin(AGREEMENT_PAYMENT).on(PAYMENT_CONCEPT.ID.eq(AGREEMENT_PAYMENT.PAYMENT_CONCEPT))
					.leftJoin(CONTRACT_PAYMENT).on(PAYMENT_CONCEPT.ID.eq(CONTRACT_PAYMENT.PAYMENT_CONCEPT))
					)
			.where(PAYMENT_CONCEPT.DOMAIN.gt(0)).and(AGREEMENT_PAYMENT.ID.isNull()).and(CONTRACT_PAYMENT.ID.isNull())
			.execute();
			
			
			System.out.printf("%d unused payment concepts deleted\r\n", deleted );
			
		});
	}

	
}
