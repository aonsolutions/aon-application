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

public class PPEITUpdate implements Update {

	public static final PPEITUpdate PPE_IT_UPDATE = new PPEITUpdate();
	
	private PPEITUpdate() {
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
		.and(PAYMENT_CONCEPT.EXPRESSION.notContains("TOTAL_PPE"))
		.fetch(DEDUCTION_CONCEPT.ID)
		;

		boolean upgraded = oldPaymentConcepts.isEmpty() ;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "TOTAL_DEVENGADO; __PPE =(/*user*/APORTACION_EMPRESA_PPE/**/ * DIAS_COTIZADOS / DIAS_MES * COEFICIENTE_PARCIALIDAD ); SELF.setBaseVariable('TOTAL_PPE', __PPE); 0.00")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "APORTACION_EMPRESA_PPE * DIAS_TRABAJADOS/DIAS_MES")
			.where(PAYMENT_CONCEPT.CODE.eq("PPE"))
			.and(PAYMENT_CONCEPT.EXPRESSION.notContains("TOTAL_PPE"))
			.execute();
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "/*read-only*/ isdef TOTAL_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','TOTAL_PPE * PORCENTAJE_CGC_E / 100.00 '); TOTAL_PPE : HIDE() /**/")
			.where(SYSTEM_COST.CODE.eq("PPE_E"))
			.and(SYSTEM_COST.EXPRESSION.notContains("TOTAL_PPE"))
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
