package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

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

public class PPEInsert implements Update {

	public static final PPEInsert PPE_INSERT = new PPEInsert();
	
	private PPEInsert() {
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
		.fetch(PAYMENT_CONCEPT.ID)
		;

		boolean upgraded = !oldPaymentConcepts.isEmpty() ;
		
		if ( upgraded )
			return;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date startOf2010 = new Date(calendar.getTimeInMillis());

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "PPE")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO")
			.set(PAYMENT_CONCEPT.EXPRESSION, "TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/); 0.00")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "__PPE")
			.returning()
			.fetchOne()
			.getId();

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)9)
			.set(SYSTEM_COST.CODE, "PPE_E")
			.set(SYSTEM_COST.START_DATE, startOf2010)
			.set(SYSTEM_COST.DESCRIPTION, "APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO")
			.set(SYSTEM_COST.EXPRESSION, "/*read-only*/ isdef BASE_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','BASE_PPE * PORCENTAJE_CGC_E / 100.00 '); BASE_PPE : HIDE() /**/")
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
