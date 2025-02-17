package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
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

import com.esferalia.aon.jooq.tables.SystemPayment;

import net.aonsolutions.db.up2date.Update;

public class PartialRetirementInsert implements Update {

	public static final PartialRetirementInsert PARTIAL_RETIREMENT_INSERT = new PartialRetirementInsert();
	
	private PartialRetirementInsert() {
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
		.and(PAYMENT_CONCEPT.CODE.eq("JUBILACION_PARCIAL"))
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
			
			String retirementBase =
					"TOTAL_DEVENGADO;"
					+ "if(!(isdef BASE_JUBILACION)){ "
					+ 	"SELF.addVariable('BASE_JUBILACION',BASE_CGC / COEFICIENTE_PARCIALIDAD);"
					+ "}"
					+ "DIAS_TRABAJADOS;"
					+ "0.00; ";

			

			Integer partialRetirementConceptId = 
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "JUBILACION_PARCIAL")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "COTIZACIÓN 100% JUBILACIÓN PARCIAL")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*default*//*read-only*/"+ retirementBase + "/**/" )
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "/*fixBaseCgcMin*/BASE_JUBILACION - BASE_CGC")
			.returning()
			.fetchOne()
			.getId();

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, partialRetirementConceptId)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*default*//*read-only*/if (\"540\".indexOf(TC2) >= 0  ){"+ retirementBase + "}else{HIDE();}/**/")
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
