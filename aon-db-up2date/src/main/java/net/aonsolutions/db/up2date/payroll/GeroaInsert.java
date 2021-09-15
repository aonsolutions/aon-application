package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class GeroaInsert implements Update {

	public static final GeroaInsert GEROA_INSERT = new GeroaInsert();
	private static final String GEROA = "GEROA";
	private static final String GEROA_PENTSIOAK_BGAE_EPSV = "GEROA PENTSIOAK BGAE/EPSV";
	private GeroaInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		List<Integer> oldPaymentConcepts =
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq(GEROA))
		.fetch(PAYMENT_CONCEPT.ID)
		;

		List<Integer> oldDeductionConcepts =
		dslContext
		.select(DEDUCTION_CONCEPT.ID)
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq(GEROA))
		.fetch(DEDUCTION_CONCEPT.ID)
		;

		boolean upgraded = !oldPaymentConcepts.isEmpty() 
				&& !oldDeductionConcepts.isEmpty();
		
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
		
		LocalDate startDate2010 = new Date(calendar.getTimeInMillis()).toLocalDate();

		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)33)
			.set(PAYMENT_CONCEPT.CODE, GEROA)
			.set(PAYMENT_CONCEPT.DESCRIPTION, GEROA_PENTSIOAK_BGAE_EPSV)
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/TOTAL_DEVENGADO * PORCENTAJE_GEROA * 0.00/**/")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "BASE_CGC * PORCENTAJE_GEROA/100.00")
			.returning()
			.fetchOne()
			.getId();
			
//			dslContext
//			.update(CONTRACT_PAYMENT)
//			.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT, newPaymentConcept)
//			.where(CONTRACT_PAYMENT.PAYMENT_CONCEPT.in(oldPaymentConcepts))
//			.execute();
//
//			dslContext
//			.update(AGREEMENT_PAYMENT)
//			.set(AGREEMENT_PAYMENT.PAYMENT_CONCEPT, newPaymentConcept)
//			.where(AGREEMENT_PAYMENT.PAYMENT_CONCEPT.in(oldPaymentConcepts))
//			.execute();
//			
//			dslContext
//			.delete(PAYMENT_CONCEPT)
//			.where(PAYMENT_CONCEPT.ID.in(oldPaymentConcepts))
//			.execute();

			int newDeductionConcept = dslContext
			.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.TYPE, (byte)9)
			.set(DEDUCTION_CONCEPT.CODE, GEROA)
			.returning()
			.fetchOne()
			.getId()
			;
			
//			dslContext
//			.delete(SYSTEM_DEDUCTION)
//			.where(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(oldDeductionConcepts))
//			.execute();
//
//			dslContext
//			.delete(DEDUCTION_CONCEPT)
//			.where(DEDUCTION_CONCEPT.ID.in(oldDeductionConcepts))
//			.execute();

			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.START_DATE, startDate2010)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, newDeductionConcept )
			.set(SYSTEM_DEDUCTION.DESCRIPTION, GEROA_PENTSIOAK_BGAE_EPSV)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "/*read-only*/ isdef BASE_GEROA ? (BASE_CGC - BASE_GEROA) * PORCENTAJE_GEROA/100.00 : HIDE() /**/")
			.execute();
			
//			dslContext
//			.delete(SYSTEM_COST)
//			.where(SYSTEM_COST.CODE.eq("GEROA_E"))
//			.execute();

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)9)
			.set(SYSTEM_COST.CODE, "GEROA_E")
			.set(SYSTEM_COST.START_DATE, startDate2010)
			.set(SYSTEM_COST.DESCRIPTION, GEROA_PENTSIOAK_BGAE_EPSV)
			.set(SYSTEM_COST.EXPRESSION, "/*read-only*/ isdef BASE_GEROA ? (BASE_CGC - BASE_GEROA) * PORCENTAJE_GEROA/100.00 : HIDE() /**/")
			.execute();
			
//			dslContext
//			.insertInto(SYSTEM_DATA)
//			.set(SYSTEM_DATA.DOMAIN, 0)
//			.set(SYSTEM_DATA.START_DATE, _2010StartDate)
//			.set(SYSTEM_DATA.NAME, "PORCENTAJE_GEROA")
//			.set(SYSTEM_DATA.EXPRESSION, "2.00")
//			.execute();
//			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
