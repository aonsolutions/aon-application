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

public class PPEUpdate implements Update {

	public static final PPEUpdate PPE_UPDATE = new PPEUpdate();
	
	private PPEUpdate() {
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
		.select(DEDUCTION_CONCEPT.ID)
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq("ATRASO_PPE"))
		.fetch(DEDUCTION_CONCEPT.ID)
		;

		boolean upgraded = !oldPaymentConcepts.isEmpty() ;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/ * DIAS_TRABAJADOS/DIAS_MES); 0.00")
			.where(PAYMENT_CONCEPT.CODE.eq("PPE"))
			.and(PAYMENT_CONCEPT.EXPRESSION.eq("TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/); 0.00"))
			.execute();

			dslContext
			.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.TYPE, (byte) 7 ) /* ¿ ANTICIPO ? */
			.set(DEDUCTION_CONCEPT.CODE, "ATRASO_PPE")
			.set(DEDUCTION_CONCEPT.DESCRIPTION, "COTIZACIÓN ATRASOS APORT. EMPR. AL PLAN DE PENSIONES DE EMPLEO")
			.set(DEDUCTION_CONCEPT.EXPRESSION, "ATRASOS_PPE()")
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
