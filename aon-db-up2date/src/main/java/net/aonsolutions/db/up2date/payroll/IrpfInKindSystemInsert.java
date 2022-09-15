package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

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

public class IrpfInKindSystemInsert implements Update {

	public static final IrpfInKindSystemInsert IRPFINKINDSYSTEMINSERT = new IrpfInKindSystemInsert();
	
	private IrpfInKindSystemInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		List<Integer> irpfCtaEspPaymentConcepts =
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("IRPF_CTA_ESP"))
		.fetch(PAYMENT_CONCEPT.ID)
		;

		boolean upgraded = irpfCtaEspPaymentConcepts.size() > 0;
		
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
		
		Date _2010StartDate = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			int irpfCtaEspPaymentConcept = dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "IRPF_CTA_ESP")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA")
			.returning(PAYMENT_CONCEPT.ID)
			.fetchOne().getId()
			;
			
			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, irpfCtaEspPaymentConcept )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()/**/")
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, -3)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, irpfCtaEspPaymentConcept )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()/**/")
			.execute();

			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.TYPE, (byte)8)
			.set(SYSTEM_DEDUCTION.START_DATE, _2010StartDate)
			.set(SYSTEM_DEDUCTION.DESCRIPTION, "INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA")
			.set(SYSTEM_DEDUCTION.EXPRESSION, "/*read-only*/ isdef BASE_CTA_ESP ? IRPF_CTA_ESP : HIDE() /**/")
			.newRecord()
			.set(SYSTEM_DEDUCTION.DOMAIN, -3)
			.set(SYSTEM_DEDUCTION.TYPE, (byte)8)
			.set(SYSTEM_DEDUCTION.START_DATE, _2010StartDate)
			.set(SYSTEM_DEDUCTION.DESCRIPTION, "INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA")
			.set(SYSTEM_DEDUCTION.EXPRESSION, "/*read-only*/ isdef BASE_CTA_ESP ? IRPF_CTA_ESP : HIDE() /**/")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
