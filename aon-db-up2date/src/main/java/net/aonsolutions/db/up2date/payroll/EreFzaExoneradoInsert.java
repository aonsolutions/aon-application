package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class EreFzaExoneradoInsert implements Update {

	public static final EreFzaExoneradoInsert EREFZAEXONERADOINSERT = new EreFzaExoneradoInsert();
	
	private EreFzaExoneradoInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("ERE_FZA_EXONERADO"))
		) >= 1;
		
		
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


			int ereFzaConceptId = 
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "ERE_FZA_EXONERADO")
			.returning()
			.fetchOne()
			.getId();


			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte)1)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, ereFzaConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "EXPDTE. REG. DE EMPLEO POR FZA. MAYOR EXONERADO")
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			"SELF.addBonus('EXPDTE. REG. DE EMPLEO POR FZA. MAYOR EXONERADO','CUOTA_EMPRESARIAL * COEFICIENTE_ERE_FZA_EXONERADO * (isdef PORCENTAJE_EXONERADO ? PORCENTAJE_EXONERADO : 100.00)/100.00');0.00")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_ERE_FZA_EXONERADO * BASE_REGULADORA")
			.execute(); 
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
