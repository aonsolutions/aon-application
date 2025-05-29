package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class PPEITFix implements Update {

	public static final PPEITFix PPE_IT_FIX = new PPEITFix();
	
	private PPEITFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		List<Integer> oldPaymentConcepts =
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PPE"))
		.and(PAYMENT_CONCEPT.QUOTE_EXPRESSION.notContains("isdef"))
		.fetch(DEDUCTION_CONCEPT.ID)
		;

		boolean upgraded = oldPaymentConcepts.isEmpty() ;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "isdef DIAS_TRABAJADOS ? APORTACION_EMPRESA_PPE * DIAS_TRABAJADOS/DIAS_MES : 0.00 ")
			.where(PAYMENT_CONCEPT.CODE.eq("PPE"))
			.and(PAYMENT_CONCEPT.QUOTE_EXPRESSION.notContains("isdef"))
			.execute();
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
