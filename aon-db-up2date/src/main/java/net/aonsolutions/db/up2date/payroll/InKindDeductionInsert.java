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

public class InKindDeductionInsert implements Update {

	public static final InKindDeductionInsert INKIND_DEDUCTION_INSERT = new InKindDeductionInsert();
	
	private InKindDeductionInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		List<Integer> oldDeductionConcepts =
		dslContext
		.select(DEDUCTION_CONCEPT.ID)
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq("EN_ESPECIE"))
		.fetch(DEDUCTION_CONCEPT.ID)
		;

		boolean upgraded = oldDeductionConcepts.size() > 0;
		
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

			int newDeductionConcept = dslContext
			.insertInto(DEDUCTION_CONCEPT)
			.set(DEDUCTION_CONCEPT.DOMAIN, 0)
			.set(DEDUCTION_CONCEPT.TYPE, (byte)8)
			.set(DEDUCTION_CONCEPT.CODE, "EN_ESPECIE")
			.returning()
			.fetchOne()
			.getId()
			;
			
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, 0)
			.set(SYSTEM_DEDUCTION.START_DATE, _2010StartDate)
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, newDeductionConcept )
			.set(SYSTEM_DEDUCTION.DESCRIPTION, "Valor de los Productos Recibidos en Especie")
			.set(SYSTEM_DEDUCTION.EXPRESSION, "/*read-only*/ isdef _EN_ESPECIE ? _EN_ESPECIE : HIDE() /**/")
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
