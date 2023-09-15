package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class HomePercentage2023Update implements Update {

	private static final String PORCENTAJE_DESMPL = "PORCENTAJE_DESMPL";
	private static final String PORCENTAJE_DESMPL_E = "PORCENTAJE_DESMPL_E";

	public static final HomePercentage2023Update HOMEPERCENTAGE2023UPDATE = new HomePercentage2023Update();

	private HomePercentage2023Update() {
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

		Date _2023StartDate = new Date(calendar.getTimeInMillis());


		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESMPL))
		.and(SYSTEM_DATA.START_DATE.eq(_2023StartDate))) == 1;

		if ( upgraded )
			return;

		InsertSetMoreStep<SystemDataRecord> insertDESMPLPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2023StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "INDEFINIDO ? 1.55 : 1.60 " )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		.newRecord()
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_DESMPL_E)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, _2023StartDate)
		.set(SYSTEM_DATA.EXPRESSION, "INDEFINIDO ? 5.50 : 6.70" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		dslContext.transaction( (config) -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			insertDESMPLPercentage.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
