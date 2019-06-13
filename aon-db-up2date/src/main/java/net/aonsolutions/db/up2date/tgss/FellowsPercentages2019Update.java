package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemCostRecord;

import net.aonsolutions.db.up2date.Update;

public class FellowsPercentages2019Update implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowsPercentages2019Update FELLOWSPERCENTAGES2019UPDATE = new FellowsPercentages2019Update();

	private FellowsPercentages2019Update() {
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


		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.START_DATE.eq(_2019StartDate))) == 0;


		if ( upgraded )
			return;


		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, -105)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_IT")
			.set(SYSTEM_DATA.EXPRESSION, "3.27 * 100 / BASE_CGP_E")
			.set(SYSTEM_DATA.START_DATE, _2019StartDate )
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, -105)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_IMS")
			.set(SYSTEM_DATA.EXPRESSION, "2.58 * 100 / BASE_CGP_E")
			.set(SYSTEM_DATA.START_DATE, _2019StartDate )
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, -105)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_CGC_E")
			.set(SYSTEM_DATA.EXPRESSION, "51.05 * 100 / BASE_CGC_E")
			.set(SYSTEM_DATA.START_DATE, _2019StartDate )
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, -105)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_CGC")
			.set(SYSTEM_DATA.EXPRESSION, "8.49 * 100 / BASE_CGC")
			.set(SYSTEM_DATA.START_DATE, _2019StartDate )
			.execute()
			;
			
			dslContext.delete(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.START_DATE.eq(_2019StartDate))
			.execute()
			;

			dslContext.delete(SYSTEM_DEDUCTION)
			.where(SYSTEM_DEDUCTION.DOMAIN.eq(-105))
			.and(SYSTEM_DEDUCTION.START_DATE.eq(_2019StartDate))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
