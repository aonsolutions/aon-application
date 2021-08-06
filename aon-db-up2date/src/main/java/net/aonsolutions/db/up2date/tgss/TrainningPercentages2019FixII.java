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

public class TrainningPercentages2019FixII implements Update {


	public static final TrainningPercentages2019FixII TRAINNINGPERCENTAGES2021FIXII = 
	new TrainningPercentages2019FixII();

	private TrainningPercentages2019FixII() {
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
		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2018);
		Date _2018EndDate = new Date(calendar.getTimeInMillis());


		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			SelectConditionStep<Record1<Integer>> fpDeductionConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("FP"));

			
			dslContext
			.delete(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-101))
			.and(SYSTEM_COST.START_DATE.ge(_2019StartDate))
			.and(SYSTEM_COST.CODE.eq("FP_E"))
			.execute();

			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, _2018EndDate)
			.where(SYSTEM_COST.DOMAIN.eq(-101))
			.and(SYSTEM_COST.CODE.eq("FP_E"))
			.and(SYSTEM_COST.END_DATE.isNull().or(SYSTEM_COST.END_DATE.ge(_2019StartDate)))
			.execute();

			dslContext
			.delete(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-101))
			.and(SYSTEM_DEDUCTION.START_DATE.ge(_2019StartDate))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(fpDeductionConcept))
			.execute();
			
			dslContext
			.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, _2018EndDate)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-101))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(fpDeductionConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull().or(SYSTEM_DEDUCTION.END_DATE.ge(_2019StartDate)))
			.execute();
			
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, -101)
			.set(SYSTEM_DEDUCTION.START_DATE, _2019StartDate)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "REMOVE()" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, fpDeductionConcept)
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, -101)
			.set(SYSTEM_COST.TYPE, (byte) 3 )
			.set(SYSTEM_COST.CODE, "FP_E" )
			.set(SYSTEM_COST.START_DATE, _2019StartDate)
			.set(SYSTEM_COST.DESCRIPTION, "FP")
			.set(SYSTEM_COST.EXPRESSION, "REMOVE()" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
