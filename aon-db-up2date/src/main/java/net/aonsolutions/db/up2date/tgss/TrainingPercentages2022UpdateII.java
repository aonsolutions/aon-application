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

public class TrainingPercentages2022UpdateII implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final TrainingPercentages2022UpdateII TRAINNINGPERCENTAGES2022UPDATEII = new TrainingPercentages2022UpdateII();

	private TrainingPercentages2022UpdateII() {
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
		calendar.set(Calendar.YEAR, 2022);

		Date start2022Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-101))
			.and(SYSTEM_COST.CODE.eq("IT_E"))
			.and(SYSTEM_COST.START_DATE.eq(start2022Date))) > 0;


		if ( upgraded )
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2021);
		Date end2021Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			SelectConditionStep<Record1<Integer>> desmplConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.DOMAIN.eq(0)).and(DEDUCTION_CONCEPT.CODE.eq("DESMPL"));
			
			// Close old  DESMPL
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, end2021Date)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-101))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(desmplConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull())
			.execute();
			
			// Insert new DESMPL
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, -101)
			.set(SYSTEM_DEDUCTION.START_DATE, start2022Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_DEVENGADO > 0.00 ? 18.08 : 0.00" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, desmplConcept )
			.execute()
			;
			
			SelectConditionStep<Record1<Integer>> fpConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.DOMAIN.eq(0)).and(DEDUCTION_CONCEPT.CODE.eq("FP"));
			
			// Close old  FP
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, end2021Date)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-101))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(fpConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull())
			.execute();
			
			// Insert new FP
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, -101)
			.set(SYSTEM_DEDUCTION.START_DATE, start2022Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "REMOVE()" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, fpConcept )
			.execute()
			;

			// Close old  FP_E, DESMPL_E, IT_E & IMS_E
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2021Date)
			.where(SYSTEM_COST.DOMAIN.eq(-101))
			.and(SYSTEM_COST.CODE.in("FP_E", "DESMPL_E", "IT_E", "IMS_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new DESMPL_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, -101)
			.set(SYSTEM_COST.TYPE, (byte) 2 )
			.set(SYSTEM_COST.CODE, "DESMPL_E" )
			.set(SYSTEM_COST.START_DATE, start2022Date)
			.set(SYSTEM_COST.DESCRIPTION, "Desempleo")
			.set(SYSTEM_COST.EXPRESSION, "64.17" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, -101)
			.set(SYSTEM_COST.TYPE, (byte) 3 )
			.set(SYSTEM_COST.CODE, "FP_E" )
			.set(SYSTEM_COST.START_DATE, start2022Date)
			.set(SYSTEM_COST.DESCRIPTION, "FP")
			.set(SYSTEM_COST.EXPRESSION, "REMOVE()" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.newRecord()
			// Insert new IT_E
			.set(SYSTEM_COST.DOMAIN, -101)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IT_E" )
			.set(SYSTEM_COST.START_DATE, start2022Date)
			.set(SYSTEM_COST.DESCRIPTION, "IT")
			.set(SYSTEM_COST.EXPRESSION, "3.64" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.newRecord()
			// Insert new IMS_E
			.set(SYSTEM_COST.DOMAIN, -101)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IMS_E" )
			.set(SYSTEM_COST.START_DATE, start2022Date)
			.set(SYSTEM_COST.DESCRIPTION, "IMS")
			.set(SYSTEM_COST.EXPRESSION, "2.87" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
