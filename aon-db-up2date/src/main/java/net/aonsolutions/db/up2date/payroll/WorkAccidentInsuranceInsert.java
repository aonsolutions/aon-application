package net.aonsolutions.db.up2date.payroll;

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

public class WorkAccidentInsuranceInsert implements Update {

	public static final WorkAccidentInsuranceInsert WORKACCIDENTINSURANCEINSERT = new WorkAccidentInsuranceInsert();
	
	private WorkAccidentInsuranceInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		List<Integer> insurancePaymentConcept =
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("SEGURO_AT"))
		.fetch(PAYMENT_CONCEPT.ID)
		;

		boolean upgraded = insurancePaymentConcept.size() > 0 ;
		
		if ( upgraded )
			return;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)13)
			.set(PAYMENT_CONCEPT.CODE, "SEGURO_AT")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "0.00")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "SEGURO ACCIDENTE DE TRABAJO")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*user*/0.00/**/ * DIAS_TRABAJADOS / DIAS_MES")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
