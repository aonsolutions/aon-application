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

import com.esferalia.aon.jooq.tables.SystemDeduction;

import net.aonsolutions.db.up2date.Update;

public class FellowsPercentages2021Update implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowsPercentages2021Update FELLOWSPERCENTAGES2021UPDATE = new FellowsPercentages2021Update();

	private FellowsPercentages2021Update() {
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
		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.YEAR, 2021);

		Date startSeptember2019Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.START_DATE.eq(startSeptember2019Date))) > 0;


		if ( upgraded )
			return;

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.YEAR, 2021);
		Date endAugust2021Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			SelectConditionStep<Record1<Integer>> cgcConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.DOMAIN.eq(0)).and(DEDUCTION_CONCEPT.CODE.eq("CGC"));
			
			// Close old  CGC
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.END_DATE, endAugust2021Date)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-105))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(cgcConcept))
			.and(SYSTEM_DEDUCTION.END_DATE.isNull())
			.execute();
			
			// Insert new CGC
			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, -105)
			.set(SYSTEM_DEDUCTION.START_DATE, startSeptember2019Date)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_DEVENGADO > 0.00 ? 9.10 : 0.00" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, cgcConcept )
			.execute()
			;
			
			// Close old  CGC_E
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, endAugust2021Date)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.in("CGC_E"))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute();

			// Insert new CGC_E
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, -105)
			.set(SYSTEM_COST.TYPE, (byte) 0 )
			.set(SYSTEM_COST.CODE, "CGC_E" )
			.set(SYSTEM_COST.START_DATE, startSeptember2019Date)
			.set(SYSTEM_COST.DESCRIPTION, "CGC")
			.set(SYSTEM_COST.EXPRESSION, "45.63" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_COST.END_DATE))
			.execute()
			;
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
