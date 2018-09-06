package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemCostRecord;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class FellowBases2018Fix implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowBases2018Fix FELLOWBASES2018FIX = new FellowBases2018Fix();
	
	private FellowBases2018Fix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2018);
		
		Date _2018StartDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-105))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))) == 0;
		
		List<Integer> cgcDeductions = 
		dslContext
		.select()
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq("CGC"))
		.fetch(DEDUCTION_CONCEPT.ID);
		;

		if ( upgraded ) 
			return;

		DeleteConditionStep<SystemDataRecord> deleteCgpBaseMin = 
		dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-105))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		;

		UpdateConditionStep<SystemDeductionRecord> updateCgcDeduction = 
		dslContext
		.update(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.EXPRESSION, "6.94" )
		.where(SYSTEM_DEDUCTION.DOMAIN.eq(-105))
		.and(SYSTEM_DEDUCTION.START_DATE.eq(_2018StartDate))
		.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(cgcDeductions.get(0)))
		;

		UpdateConditionStep<SystemCostRecord> updateCgcCosts = 
		dslContext
		.update(SYSTEM_COST)
		.set(SYSTEM_COST.EXPRESSION, "34.80")
		.where(SYSTEM_COST.DOMAIN.eq(-105))
		.and(SYSTEM_COST.CODE.eq("CGC_E"))
		.and(SYSTEM_COST.START_DATE.eq(_2018StartDate))
		;

		UpdateConditionStep<SystemCostRecord> updateITCosts = 
		dslContext
		.update(SYSTEM_COST)
		.set(SYSTEM_COST.EXPRESSION, "2.67")
		.where(SYSTEM_COST.DOMAIN.eq(-105))
		.and(SYSTEM_COST.CODE.eq("IT_E"))
		.and(SYSTEM_COST.START_DATE.eq(_2018StartDate))
		;

		UpdateConditionStep<SystemCostRecord> updateIMSCosts = 
		dslContext
		.update(SYSTEM_COST)
		.set(SYSTEM_COST.EXPRESSION, "2.11")
		.where(SYSTEM_COST.DOMAIN.eq(-105))
		.and(SYSTEM_COST.CODE.eq("IMS_E"))
		.and(SYSTEM_COST.START_DATE.eq(_2018StartDate))
		;


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			deleteCgpBaseMin.execute();
			updateCgcDeduction.execute();
			updateCgcCosts.execute();
			updateITCosts.execute();
			updateIMSCosts.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
