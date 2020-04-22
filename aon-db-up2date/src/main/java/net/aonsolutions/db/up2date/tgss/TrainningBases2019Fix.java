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
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemCostRecord;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class TrainningBases2019Fix implements Update {


	public static final TrainningBases2019Fix TRAINNINGBASES2019FIX = new TrainningBases2019Fix();
	
	private TrainningBases2019Fix() {
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
		calendar.set(Calendar.YEAR, 2019);
		
		Date _2019StartDate = new Date(calendar.getTimeInMillis());


		calendar.add(Calendar.YEAR, -1);
		Date _2018StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2018EndDate = new Date(calendar.getTimeInMillis());

		
		List<Integer> cgcDeductions = 
		dslContext
		.select()
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq("CGC"))
		.fetch(DEDUCTION_CONCEPT.ID);
		;

		
		boolean upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-101))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(cgcDeductions))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-101))
			.and(SYSTEM_COST.CODE.eq("CGC_E"))
			.and(SYSTEM_COST.START_DATE.eq(_2019StartDate))) == 1;


		if ( upgraded ) 
			return;

		// CLOSE 2018 CGC
		UpdateConditionStep<SystemDeductionRecord> closeCgcDeductions = dslContext
		.update(SYSTEM_DEDUCTION)
		.set( SYSTEM_DEDUCTION.END_DATE, _2018EndDate)
		.where(SYSTEM_DEDUCTION.DOMAIN.in(-101))
		.and(SYSTEM_DEDUCTION.END_DATE.isNull())
		.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(cgcDeductions))
		;

		// CLOSE 2018 CGC_E
		UpdateConditionStep<SystemCostRecord> closeCgcCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2018EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-101))
		.and(SYSTEM_COST.CODE.eq("CGC_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;
		
		// CLOSE 2018 FOGASA_E
		UpdateConditionStep<SystemCostRecord> closeFogasaCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2018EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-101))
		.and(SYSTEM_COST.CODE.eq("FOGASA_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;

		InsertSetMoreStep<SystemDeductionRecord> insertCgcDeduction = dslContext
		.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.DOMAIN, -101)
		.set(SYSTEM_DEDUCTION.START_DATE, _2019StartDate)
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, cgcDeductions.get(0))
		.set(SYSTEM_DEDUCTION.EXPRESSION, "8.49" )
		.set(SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE, ( byte) 1 )
		.set(SYSTEM_DEDUCTION.TYPE, DSL.castNull(SYSTEM_DEDUCTION.TYPE))
		.set(SYSTEM_DEDUCTION.MONTH, DSL.castNull(SYSTEM_DEDUCTION.MONTH))
		.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		.set(SYSTEM_DEDUCTION.DESCRIPTION, DSL.castNull(SYSTEM_DEDUCTION.DESCRIPTION))
		;


		InsertSetMoreStep<SystemCostRecord> insertCgcCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -101)
		.set(SYSTEM_COST.TYPE, (byte) 0 )
		.set(SYSTEM_COST.CODE, "CGC_E" )
		.set(SYSTEM_COST.START_DATE, _2019StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "Contingencias Comunes")
		.set(SYSTEM_COST.EXPRESSION, "42.56" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;

		InsertSetMoreStep<SystemCostRecord> insertFogasaCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -101)
		.set(SYSTEM_COST.TYPE, (byte) 3 )
		.set(SYSTEM_COST.CODE, "FOGASA_E" )
		.set(SYSTEM_COST.START_DATE, _2019StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "FOGASA")
		.set(SYSTEM_COST.EXPRESSION, "3.23" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			closeCgcDeductions.execute();
			closeCgcCosts.execute();
			closeFogasaCosts.execute();
			
			insertCgcDeduction.execute();
			insertCgcCost.execute();
			insertFogasaCost.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
