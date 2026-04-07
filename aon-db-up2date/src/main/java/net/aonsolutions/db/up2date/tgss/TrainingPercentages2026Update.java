package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
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

import net.aonsolutions.db.up2date.Update;

public class TrainingPercentages2026Update implements Update {

	private static final int DOMAIN = -101;

	public static final TrainingPercentages2026Update TRAINNINGPERCENTAGES2026UPDATE = new TrainingPercentages2026Update();

	private TrainingPercentages2026Update() {
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
		calendar.set(Calendar.YEAR, 2026);

		Date start2026Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.eq("CGC_E"))
			.and(SYSTEM_COST.START_DATE.eq(start2026Date))) > 0;


		if ( upgraded )
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2025);
		Date end2025Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// ---------------------------------------------------------
			// CGC
			
			SelectConditionStep<Record1<Integer>> cgcConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("CGC"));
			
			// Close old  CGC
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, end2025Date)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(cgcConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull())
			.execute();

			// Insert new CGC
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, start2026Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_BASE_CGC > 0.00 ? 11.51 : 0.00" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, cgcConcept )
			.execute();
			
			// Close old  CGC_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2025Date)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("CGC_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new CGC_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 0 )
			.set(SYSTEM_COST.CODE, "CGC_E" )
			.set(SYSTEM_COST.START_DATE, start2026Date)
			.set(SYSTEM_COST.DESCRIPTION, "CGC")
			.set(SYSTEM_COST.EXPRESSION, "TOTAL_BASE_CGC_E > 0.00 ? 57.72 : 0.00" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute();

			// MEI
			
			// Close old  MEI_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.TYPE, (byte) 13 )
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("MEI_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();


			// ---------------------------------------------------------
			// FP
			
			SelectConditionStep<Record1<Integer>> fpConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("FP"));
			
			// Close old  FP
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, end2025Date)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(fpConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull())
			.execute();

			// Insert new FP
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, start2026Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_BASE_CGP > 0.00 ? 0.28 : 0.00" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, fpConcept )
			.execute();
			
			// Close old  FP_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2025Date)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("FP_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new FP_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 3 )
			.set(SYSTEM_COST.CODE, "FP_E" )
			.set(SYSTEM_COST.START_DATE, start2026Date)
			.set(SYSTEM_COST.DESCRIPTION, "FP")
			.set(SYSTEM_COST.EXPRESSION, "TOTAL_BASE_CGP_E > 0.00 ? 2.16 : 0.00" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute();

			// ---------------------------------------------------------
			// DESMPL
			
			SelectConditionStep<Record1<Integer>> desmplConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("DESMPL"));
			
			// Close old  DESMPL
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, end2025Date)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(desmplConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull())
			.execute();

			// Insert new DESMPL
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, DOMAIN)
			.set(SYSTEM_DEDUCTION.START_DATE, start2026Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_BASE_CGP > 0.00 ? 1424.40 * (isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=1.55)/100 : 0.00" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, desmplConcept )
			.execute();

			// Close old  DESMPL_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2025Date)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("DESMPL_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();


			// Insert new DESMPL_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 2 )
			.set(SYSTEM_COST.CODE, "DESMPL_E" )
			.set(SYSTEM_COST.START_DATE, start2026Date)
			.set(SYSTEM_COST.DESCRIPTION, "DESMPL")
			.set(SYSTEM_COST.EXPRESSION, "TOTAL_BASE_CGP_E > 0.00 ? 1424.40 * (isdef PORCENTAJE_DESMPL_E ? PORCENTAJE_DESMPL_E : PORCENTAJE_DESMPL_E=5.50)/100 : 0.00" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute();

			// ---------------------------------------------------------
			// FOGASA
			
			// Close old  FOGASA_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2025Date)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("FOGASA_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new FOGASA_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 10 )
			.set(SYSTEM_COST.CODE, "FOGASA_E" )
			.set(SYSTEM_COST.START_DATE, start2026Date)
			.set(SYSTEM_COST.DESCRIPTION, "FOGASA")
			.set(SYSTEM_COST.EXPRESSION, "TOTAL_BASE_CGP_E > 0.00 ? 4.38 : 0.00" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;
			
			// ---------------------------------------------------------
			// IT
			
			// Close old  IT_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2025Date)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("IT_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new IT_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IT_E" )
			.set(SYSTEM_COST.START_DATE, start2026Date)
			.set(SYSTEM_COST.DESCRIPTION, "IT")
			.set(SYSTEM_COST.EXPRESSION, "TOTAL_BASE_CGP_E > 0.00 ? 4.12 : 0.00" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;

			// ---------------------------------------------------------
			// IMS
			
			// Close old  IMS_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2025Date)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("IMS_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new IMS_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IMS_E" )
			.set(SYSTEM_COST.START_DATE, start2026Date)
			.set(SYSTEM_COST.DESCRIPTION, "IMS")
			.set(SYSTEM_COST.EXPRESSION, "TOTAL_BASE_CGP_E > 0.00 ? 3.83 : 0.00" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
