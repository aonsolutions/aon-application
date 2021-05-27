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

public class InKindDeductionInsertRETA implements Update {

	public static final InKindDeductionInsertRETA INKIND_DEDUCTION_INSERT_RETA = new InKindDeductionInsertRETA();
	
	private InKindDeductionInsertRETA() {
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
		.select(SYSTEM_DEDUCTION.ID)
		.from(SYSTEM_DEDUCTION)
		.innerJoin(DEDUCTION_CONCEPT).onKey()
		.where(SYSTEM_DEDUCTION.DOMAIN.eq(-3))
		.and(DEDUCTION_CONCEPT.CODE.eq("EN_ESPECIE")))
		>= 1;
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.columns(
			SYSTEM_DEDUCTION.DOMAIN
			,SYSTEM_DEDUCTION.EXPRESSION
			,SYSTEM_DEDUCTION.START_DATE
			,SYSTEM_DEDUCTION.DESCRIPTION
			,SYSTEM_DEDUCTION.DEDUCTION_CONCEPT)
			.select(DSL.select(
			DSL.cast(-3, Integer.class)
			,SYSTEM_DEDUCTION.EXPRESSION
			,SYSTEM_DEDUCTION.START_DATE
			,SYSTEM_DEDUCTION.DESCRIPTION
			,SYSTEM_DEDUCTION.DEDUCTION_CONCEPT)
			.from(SYSTEM_DEDUCTION)
			.innerJoin(DEDUCTION_CONCEPT).onKey()
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("EN_ESPECIE")))
			.execute()
			;

			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
