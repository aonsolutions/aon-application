package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementData.AGREEMENT_DATA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.sql.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.AgreementData;

import net.aonsolutions.db.up2date.Update;

public class DefaultAgreementUpdate2 implements Update {
	
	private static final Date EPOCH = new Date(0);
	private static final String ESTATUTO_DE_LOS_TRABAJADORES = "ESTATUTO DE LOS TRABAJADORES";

	public static final DefaultAgreementUpdate2 DEFAULTAGREEMENTUPDATE2 = new DefaultAgreementUpdate2();
	
	private DefaultAgreementUpdate2() {
	}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);


		int defAgreemetId =  	
				dslContext
				.select()
				.from(AGREEMENT_PAYMENT)
				.where(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
				.and(AGREEMENT_PAYMENT.DESCRIPTION.eq("SHOW_SALARIO_MENSUAL"))
				.fetchOptional(AGREEMENT_PAYMENT.AGREEMENT)
				.orElse(Integer.MIN_VALUE);
			
		boolean upgraded = defAgreemetId == 0;
			
		if ( upgraded ) 
			return;

		int salarioBaseId = 
		dslContext
		.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("SALARIO_BASE"))
		.and(PAYMENT_CONCEPT.DESCRIPTION.eq("SALARIO BASE MENSUAL"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			// ----------------------------------------------------------------

			dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.EXPRESSION, 
			"/*default*//*read-only*/ "
			+ "isdef SALARIO_MENSUAL ? ( SALARIO_MENSUAL  * DIAS_TRABAJADOS / DIAS_MES )"
			+ " : isdef DIAS_TRABAJADOS ? BASE_CGC_MIN * 12 / 14: REMOVE())"
			+ "/**/")
			.where(AGREEMENT_PAYMENT.DOMAIN.eq(0))
			.and(AGREEMENT_PAYMENT.AGREEMENT.eq(0))
			.and(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.eq(salarioBaseId))
			.execute();
			
			dslContext
			.insertInto(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.DOMAIN, 0)
			.set(AGREEMENT_PAYMENT.AGREEMENT, 0)
			.set(AGREEMENT_PAYMENT.TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.START_DATE, EPOCH)
			.set(AGREEMENT_PAYMENT.SALARY_TYPE, (byte) 0)
			.set(AGREEMENT_PAYMENT.DESCRIPTION, "SHOW_SALARIO_MENSUAL")
			.set(AGREEMENT_PAYMENT.EXPRESSION, "/*read-only*/SALARIO_MENSUAL * 0.00; HIDE()/**/")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

	
}
