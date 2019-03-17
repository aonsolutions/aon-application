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

public class FellowsBases2019Update implements Update {

	private static final String BASE_CGP_MIN = "BASE_CGP_MIN";

	public static final FellowsBases2019Update FELLOWSBASES2019UPDATE = new FellowsBases2019Update();

	private FellowsBases2019Update() {
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


		calendar.add(Calendar.YEAR, -1);
		Date _2018StartDate = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		Date _2018EndDate = new Date(calendar.getTimeInMillis());

		boolean	upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.eq("IT_E"))
			.and(SYSTEM_COST.START_DATE.eq(_2019StartDate))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(-105))
			.and(SYSTEM_COST.CODE.eq("IMS_E"))
			.and(SYSTEM_COST.START_DATE.eq(_2019StartDate))) == 1;


		if ( upgraded )
			return;

		// CLOSE 2018 IT_E
		UpdateConditionStep<SystemCostRecord> closeITCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2018EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-105))
		.and(SYSTEM_COST.CODE.eq("IT_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;

		// CLOSE 2018 IMS_E
		UpdateConditionStep<SystemCostRecord> closeIMSCosts = dslContext
		.update(SYSTEM_COST)
		.set( SYSTEM_COST.END_DATE, _2018EndDate)
		.where(SYSTEM_COST.DOMAIN.in(-105))
		.and(SYSTEM_COST.CODE.eq("IMS_E"))
		.and(SYSTEM_COST.END_DATE.isNull())
		;


		InsertSetMoreStep<SystemCostRecord> insertITCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -105)
		.set(SYSTEM_COST.TYPE, (byte) 1 )
		.set(SYSTEM_COST.CODE, "IT_E" )
		.set(SYSTEM_COST.START_DATE, _2019StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "IT")
		.set(SYSTEM_COST.EXPRESSION, "3.27" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;

		InsertSetMoreStep<SystemCostRecord> insertIMSCost = dslContext
		.insertInto(SYSTEM_COST)
		.set(SYSTEM_COST.DOMAIN, -105)
		.set(SYSTEM_COST.TYPE, (byte) 1 )
		.set(SYSTEM_COST.CODE, "IMS_E" )
		.set(SYSTEM_COST.START_DATE, _2019StartDate)
		.set(SYSTEM_COST.DESCRIPTION, "IMS")
		.set(SYSTEM_COST.EXPRESSION, "2.58" )
		.set(SYSTEM_COST.END_DATE, DSL.castNull(SYSTEM_DEDUCTION.END_DATE))
		;

		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			closeITCosts.execute();
			closeIMSCosts.execute();

			insertITCost.execute();
			insertIMSCost.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
