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

public class TrainingUnemployment2023Fix implements Update {

	private static final int DOMAIN = -101;

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final TrainingUnemployment2023Fix TRAININGUNEMPLOYMENT2023FIX = new TrainingUnemployment2023Fix();

	private TrainingUnemployment2023Fix() {
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
		calendar.set(Calendar.YEAR, 2023);

		Date start2023Date = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.eq("DESMPL_E"))
			.and(SYSTEM_COST.START_DATE.eq(start2023Date))
			.and(SYSTEM_COST.EXPRESSION.contains("1260.00"))
			) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			

			// ---------------------------------------------------------
			// DESMPL
			
			SelectConditionStep<Record1<Integer>> desmplConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("DESMPL"));
			
			// Update DESMPL
			dslContext.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, DSL.replace(SYSTEM_DEDUCTION.EXPRESSION, BASE_CGP_MIN, "1260.00") )
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(desmplConcept))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(start2023Date))
			.execute();

			// Update DESMPL_E 
			dslContext.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, DSL.replace(SYSTEM_COST.EXPRESSION, BASE_CGP_MIN, "1260.00")  )
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.CODE.in("DESMPL_E"))
			.and(SYSTEM_COST.START_DATE.eq(start2023Date))
			.execute();



			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
