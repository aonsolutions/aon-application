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

public class TrainingMEI2026Fix implements Update {

	private static final int DOMAIN = -101;

	public static final TrainingMEI2026Fix TRAININGMEI2026FIX = new TrainingMEI2026Fix();

	private TrainingMEI2026Fix() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

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
			.and(SYSTEM_COST.CODE.eq("MEI_E"))
			.and(SYSTEM_COST.EXPRESSION.contains("10.68"))
			.and(SYSTEM_COST.START_DATE.eq(start2026Date))) > 0;


		if ( upgraded )
			return;

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			

			// ---------------------------------------------------------
			// MEI
			
			SelectConditionStep<Record1<Integer>> meiConcept = 
			DSL.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq("MEI"));
			

			// Fix MEI
			dslContext
			.update(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "1424.40 * PORCENTAJE_MEI / 100.00; TOTAL_BASE_CGC > 0.00 ? 2.14 : 0.00" )
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(start2026Date))
			.and(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT.eq(meiConcept))
			.execute();
			
			// Fix MEI_E
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "1424.40 * PORCENTAJE_MEI_E / 100.00; TOTAL_BASE_CGC_E > 0.00 ? 10.68 : 0.00" )
			.where(SYSTEM_COST.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_COST.TYPE.eq((byte) 13))
			.and(SYSTEM_COST.CODE.eq("MEI_E"))
			.and(SYSTEM_COST.START_DATE.eq(start2026Date))
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
