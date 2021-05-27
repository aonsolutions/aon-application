package net.aonsolutions.db.up2date.payroll;

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

public class IndemnizacionEditableUpdate implements Update {

	public static final IndemnizacionEditableUpdate INDEMNIZACIONEDITABLEUPDATE = new IndemnizacionEditableUpdate();
	
	private IndemnizacionEditableUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);		
		
		List<Integer> indemnizacionConceptIds = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("INDEMNIZACION"))
		.fetch(PAYMENT_CONCEPT.ID);
		
		boolean upgraded = 
		dslContext.fetchCount(
		DSL
		.select()
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
		.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
		.andNot(SYSTEM_PAYMENT.EXPRESSION.contains("/*default*/"))
		) == 0;
		
		if ( upgraded )
			return;
		
		
		dslContext.transaction( (config) -> {	
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "20 * AÑOS_TRABAJADOS * SALARIO_DIA" )  
			.where(PAYMENT_CONCEPT.ID.in(indemnizacionConceptIds))
			.execute();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/", "/*default*/") )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
			.execute();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "/**/", "") )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))			
			.execute();
			

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "?", "?/*user*/") )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
			.execute();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, ":", "/**/:") )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
			.execute();


			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "-INDEMNIZACION)", "-(isdef INDEMNIZACION ? INDEMNIZACION : 0.00))") )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
			.execute();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.replace(SYSTEM_PAYMENT.EXPRESSION, "HIDE()", "__HIDE_") )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
			.execute();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.concat(SYSTEM_PAYMENT.EXPRESSION, DSL.cast(SYSTEM_PAYMENT.ID, String.class)) )  
			.where(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(indemnizacionConceptIds))
			.execute();		});
	}

}
