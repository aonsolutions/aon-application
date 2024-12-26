package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemData;

import net.aonsolutions.db.up2date.Update;

public class TrainingExcessDeductions2024Insert implements Update {

	private static final int DOMAIN = -101;

	public static final TrainingExcessDeductions2024Insert TRAININGEXCESSDEDUCTIONS2024INSERT = new TrainingExcessDeductions2024Insert();

	private TrainingExcessDeductions2024Insert() {
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
		calendar.set(Calendar.YEAR, 2024);

		Date start2024Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2023);
		Date end2023Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.EXPRESSION.like("%BASE_EXCESO%"))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(start2024Date))) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// ---------------------------------------------------------
			// CGC
			
			SelectConditionStep<Record1<Integer>> cgcConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("CGC"));
			

			// Insert new CGC
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, start2024Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_CGC/100" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, cgcConcept )
			.execute();
			
			// Insert new CGC_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 0 )
			.set(SYSTEM_COST.CODE, "CGC_E" )
			.set(SYSTEM_COST.START_DATE, start2024Date)
			.set(SYSTEM_COST.DESCRIPTION, "CGC")
			.set(SYSTEM_COST.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_CGC_E/100" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute();


			// ---------------------------------------------------------
			// FP
			
			SelectConditionStep<Record1<Integer>> fpConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("FP"));

			// Close old PORCENTAJE_FP
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, end2023Date)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.END_DATE.isNull())
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_FP"))
			.execute();
			// Insert new PORCENTAJE_FP
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, DOMAIN)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_FP" )
			.set(SYSTEM_DATA.START_DATE, start2024Date)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DATA.EXPRESSION, "0.10" )
			.execute();

			// Insert new FP
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, start2024Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_FP/100" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, fpConcept )
			.execute();
			
			// Insert new FP_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 3 )
			.set(SYSTEM_COST.CODE, "FP_E" )
			.set(SYSTEM_COST.START_DATE, start2024Date)
			.set(SYSTEM_COST.DESCRIPTION, "FP")
			.set(SYSTEM_COST.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_FP_E/100" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute();

			// ---------------------------------------------------------
			// DESMPL
			
			SelectConditionStep<Record1<Integer>> desmplConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("DESMPL"));
			
			// Insert new DESMPL
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, start2024Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_DESMPL/100" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, desmplConcept )
			.execute();

			// Insert new DESMPL_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 2 )
			.set(SYSTEM_COST.CODE, "DESMPL_E" )
			.set(SYSTEM_COST.START_DATE, start2024Date)
			.set(SYSTEM_COST.DESCRIPTION, "DESMPL")
			.set(SYSTEM_COST.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_DESMPL_E/100" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute();

			// ---------------------------------------------------------
			// FOGASA
			
			// Insert new FOGASA_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 10 )
			.set(SYSTEM_COST.CODE, "FOGASA_E" )
			.set(SYSTEM_COST.START_DATE, start2024Date)
			.set(SYSTEM_COST.DESCRIPTION, "FOGASA")
			.set(SYSTEM_COST.EXPRESSION, "BASE_EXCESO * COTIZA_EXCESO * PORCENTAJE_FOGASA/ 100" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
