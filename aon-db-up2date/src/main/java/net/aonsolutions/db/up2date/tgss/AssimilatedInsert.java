package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemCostRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class AssimilatedInsert implements Update {
	
	public static final AssimilatedInsert ASSIMILATEDINSERT = new AssimilatedInsert();

	private AssimilatedInsert() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		// START_DATE 01/01/2019
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2019);
		
		Date startOf2019Date = new Date(calendar.getTimeInMillis());
		
		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_DEDUCTION)
					.where(SYSTEM_DEDUCTION.DOMAIN.eq(-104))
					.and(SYSTEM_DEDUCTION.START_DATE.eq(startOf2019Date))
				) >= 1;

		// IF ALREADY EXISTS
				
		if ( upgraded ) 
			return;
		
		SelectConditionStep<Record1<Integer>> desmplConcept = 
		DSL.select(DEDUCTION_CONCEPT.ID)
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq("DESMPL"));

		InsertSetMoreStep<SystemDeductionRecord> insertSystemDeduction = 
		dslContext.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.DOMAIN, -104)
		.set(SYSTEM_DEDUCTION.START_DATE, startOf2019Date)
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, desmplConcept)
		.set(SYSTEM_DEDUCTION.EXPRESSION, "REMOVE()");

		InsertSetMoreStep<SystemCostRecord> insertSystemCosts = 
		dslContext.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -104)
		.set(SYSTEM_COST.START_DATE, startOf2019Date)
		.set(SYSTEM_COST.TYPE, (byte)2)
		.set(SYSTEM_COST.CODE, "DESMPL_E")
		.set(SYSTEM_COST.EXPRESSION, "REMOVE()")
		.set(SYSTEM_COST.DESCRIPTION, "Desempleo")
		.newRecord()
		.set(SYSTEM_COST.DOMAIN, -104)
		.set(SYSTEM_COST.START_DATE, startOf2019Date)
		.set(SYSTEM_COST.TYPE, (byte)10)
		.set(SYSTEM_COST.CODE, "FOGASA_E")
		.set(SYSTEM_COST.EXPRESSION, "REMOVE()")
		.set(SYSTEM_COST.DESCRIPTION, "FOGASA")
		;

		// DISABLED FOREING_KEY FOR INSERT
		
		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insertSystemCosts.execute();
			insertSystemDeduction.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
