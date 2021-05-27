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

public class FellowsBases2018Update implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowsBases2018Update FELLOWSBASES2018UPDATE = new FellowsBases2018Update();
	
	private FellowsBases2018Update() {
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


		calendar.add(Calendar.YEAR, -1);
		Date _2017StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2017EndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-105))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate))) == 1;
		
		List<Integer> cgcDeductions = 
		dslContext
		.select()
		.from(DEDUCTION_CONCEPT)
		.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
		.and(DEDUCTION_CONCEPT.CODE.eq("CGC"))
		.fetch(DEDUCTION_CONCEPT.ID);
		;
		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-105))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(cgcDeductions))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.eq("CGC_E"))
			.and(SYSTEM_COST.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.eq("IT_E"))
			.and(SYSTEM_COST.START_DATE.eq(_2018StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.eq("IMS_E"))
			.and(SYSTEM_COST.START_DATE.eq(_2018StartDate))) == 1;


		if ( upgraded ) 
			return;

		// CLEAN OLD 2018
		DeleteConditionStep<SystemDataRecord> deleteData = dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-105))
		.and(SYSTEM_DATA.START_DATE.eq(_2018StartDate));

		DeleteConditionStep<SystemDeductionRecord> deleteDeductions = dslContext
		.delete(SYSTEM_DEDUCTION)
		.where(SYSTEM_DEDUCTION.DOMAIN.eq(-105))
		.and(SYSTEM_DEDUCTION.START_DATE.eq(_2018StartDate));

		DeleteConditionStep<SystemCostRecord> deleteCosts = dslContext
		.delete(SYSTEM_COST)
		.where(SYSTEM_COST.DOMAIN.eq(-105))
		.and(SYSTEM_COST.START_DATE.eq(_2018StartDate));

		// CLOSE 2017 BASE_CGP_MIN
		UpdateConditionStep<SystemDataRecord> closeBaseCgpMin = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, _2017EndDate)
		.where(SYSTEM_DATA.DOMAIN.in(-105))
		.and(SYSTEM_DATA.NAME.eq(BASE_CGP_MIN))
		.and(SYSTEM_DATA.START_DATE.eq(_2017StartDate))
		;
		// CLOSE 2017 CGC
		UpdateConditionStep<SystemDeductionRecord> closeCgcDeductions = dslContext
		.update(SYSTEM_DEDUCTION)
		.set( SYSTEM_DEDUCTION.END_DATE, _2017EndDate)
		.where(SYSTEM_DEDUCTION.DOMAIN.in(-105))
		.and(SYSTEM_DEDUCTION.END_DATE.isNull())
		.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.in(cgcDeductions))
		;

		// CLOSE 2017 CGC_E
		UpdateConditionStep<SystemCostRecord> closeCgcCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2017EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-105))
		.and(SYSTEM_COST.CODE.eq("CGC_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;

		// CLOSE 2016 & 2017 IT_E
		UpdateConditionStep<SystemCostRecord> closeITCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2017EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-105))
		.and(SYSTEM_COST.CODE.eq("IT_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;

		// CLOSE 2016 & 2017 IMS_E
		UpdateConditionStep<SystemCostRecord> closeIMSCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2017EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-105))
		.and(SYSTEM_COST.CODE.eq("IMS_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;

		InsertSetMoreStep<SystemDataRecord> insertCgpBaseMin = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -105)
		.set(SYSTEM_DATA.NAME, BASE_CGP_MIN)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "858.60" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;
		
		InsertSetMoreStep<SystemDeductionRecord> insertCgcDeduction = dslContext
		.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.DOMAIN, -105)
		.set(SYSTEM_DEDUCTION.START_DATE, _2018StartDate)
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, cgcDeductions.get(0))
		.set(SYSTEM_DEDUCTION.EXPRESSION, "SI(BASE_CGC > 0.00, 6.94,0.00)" )
		.set(SYSTEM_DEDUCTION.DESCRIPTION_DECORABLE, ( byte) 1 )
		.set(SYSTEM_DEDUCTION.TYPE, DSL.castNull(SYSTEM_DEDUCTION.TYPE))
		.set(SYSTEM_DEDUCTION.MONTH, DSL.castNull(SYSTEM_DEDUCTION.MONTH))
		.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		.set(SYSTEM_DEDUCTION.DESCRIPTION, DSL.castNull(SYSTEM_DEDUCTION.DESCRIPTION))
		;


		InsertSetMoreStep<SystemCostRecord> insertCgcCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -105)
		.set(SYSTEM_COST.TYPE, (byte) 0 )
		.set(SYSTEM_COST.CODE, "CGC_E" )
		.set(SYSTEM_COST.START_DATE, _2018StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "Contingencias Comunes")
		.set(SYSTEM_COST.EXPRESSION, "SI(BASE_CGC > 0.00, 34.80, 0.00)" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;

		InsertSetMoreStep<SystemCostRecord> insertITCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -105)
		.set(SYSTEM_COST.TYPE, (byte) 1 )
		.set(SYSTEM_COST.CODE, "IT_E" )
		.set(SYSTEM_COST.START_DATE, _2018StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "IT")
		.set(SYSTEM_COST.EXPRESSION, "SI(BASE_CGC > 0.00, 2.67, 0.00)" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;

		InsertSetMoreStep<SystemCostRecord> insertIMSCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -105)
		.set(SYSTEM_COST.TYPE, (byte) 1 )
		.set(SYSTEM_COST.CODE, "IMS_E" )
		.set(SYSTEM_COST.START_DATE, _2018StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "IMS")
		.set(SYSTEM_COST.EXPRESSION, "SI(BASE_CGC > 0.00, 2.11, 0.00)" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;



		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			deleteData.execute();
			deleteDeductions.execute();
			deleteCosts.execute();

			closeBaseCgpMin.execute();
			closeCgcDeductions.execute();
			closeCgcCosts.execute();
			closeITCosts.execute();
			closeIMSCosts.execute();
			
			insertCgpBaseMin.execute();

			insertCgcDeduction.execute();

			insertCgcCost.execute();
			insertITCost.execute();
			insertIMSCost.execute();
			
			System.out.print(" COTIZACIÓN POR CONTRATOS BECARIOS 2018 ");
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
			
		});
	}

}
