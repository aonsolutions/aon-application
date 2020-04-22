package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FellowsPercentages2019Fix implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowsPercentages2019Fix FELLOWSPERCENTAGES2019FIX = new FellowsPercentages2019Fix();

	private FellowsPercentages2019Fix() {
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



		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-105))
			.and(SYSTEM_DATA.NAME.in("PORCENTAJE_CGC", "PORCENTAJE_IT", "PORCENTAJE_IMS", "PORCENTAJE_CGC_E"))
			.and(SYSTEM_DATA.START_DATE.eq(_2019StartDate))
			.execute();
			
			dslContext
			.delete(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.START_DATE.eq(_2019StartDate))
			.execute();

			dslContext
			.delete(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-105))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(_2019StartDate))
			.execute();

			dslContext
			.insertInto(SYSTEM_DEDUCTION)
			.set(SYSTEM_DEDUCTION.DOMAIN, -105)
			.set(SYSTEM_DEDUCTION.START_DATE, _2019StartDate)
			.set(SYSTEM_DEDUCTION.EXPRESSION, "TOTAL_DEVENGADO > 0.00 ? 8.49 : 0.00" )
			.set(SYSTEM_DEDUCTION.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, DSL.select(DEDUCTION_CONCEPT.ID).from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.DOMAIN.eq(0)).and(DEDUCTION_CONCEPT.CODE.eq("CGC")) )
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, -105)
			.set(SYSTEM_COST.TYPE, (byte) 0 )
			.set(SYSTEM_COST.CODE, "CGC_E" )
			.set(SYSTEM_COST.START_DATE, _2019StartDate)
			.set(SYSTEM_COST.DESCRIPTION, "CGC")
			.set(SYSTEM_COST.EXPRESSION, "42.56" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, -105)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IT_E" )
			.set(SYSTEM_COST.START_DATE, _2019StartDate)
			.set(SYSTEM_COST.DESCRIPTION, "IT")
			.set(SYSTEM_COST.EXPRESSION, "3.27" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, -105)
			.set(SYSTEM_COST.TYPE, (byte) 1 )
			.set(SYSTEM_COST.CODE, "IMS_E" )
			.set(SYSTEM_COST.START_DATE, _2019StartDate)
			.set(SYSTEM_COST.DESCRIPTION, "IMS")
			.set(SYSTEM_COST.EXPRESSION, "2.58" )
			.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
