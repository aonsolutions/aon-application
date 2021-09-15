package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class HomePercentage2019Update implements Update {

	private static final String PORCENTAJE_IT = "PORCENTAJE_IT";
	private static final String PORCENTAJE_IMS = "PORCENTAJE_IMS";
	private static final String PORCENTAJE_CGC = "PORCENTAJE_CGC";
	private static final String PORCENTAJE_CGC_E = "PORCENTAJE_CGC_E";

	public static final HomePercentage2019Update HOMEPERCENTAGE2019UPDATE = new HomePercentage2019Update();

	private HomePercentage2019Update() {
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

		LocalDate startDate2019 = new Date(calendar.getTimeInMillis()).toLocalDate();


		calendar.add(Calendar.YEAR, -1);
		LocalDate startDate2018 = new Date(calendar.getTimeInMillis()).toLocalDate();
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		LocalDate endDate2018 = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2014);
		LocalDate startDate2014 = new Date(calendar.getTimeInMillis()).toLocalDate();

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-106))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC))
		.and(SYSTEM_DATA.START_DATE.eq(startDate2019))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_CGC_E))
			.and(SYSTEM_DATA.START_DATE.eq(startDate2019))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_IT))
			.and(SYSTEM_DATA.START_DATE.eq(startDate2019))) == 1;

		if ( upgraded )
			upgraded =
			dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(-106))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_IMS))
			.and(SYSTEM_DATA.START_DATE.eq(startDate2019))) == 1;

		if ( upgraded )
			return;


		// CLOSE 2018
		UpdateConditionStep<SystemDataRecord> close2018 = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, endDate2018)
		.where(SYSTEM_DATA.DOMAIN.in(-106))
		.and(SYSTEM_DATA.NAME.in(PORCENTAJE_CGC, PORCENTAJE_CGC_E))
		.and(SYSTEM_DATA.START_DATE.eq(startDate2018))
		;

		// CLOSE 2014 IT & IMS
		UpdateConditionStep<SystemDataRecord> closeITIMS = dslContext
		.update(SYSTEM_DATA)
		.set( SYSTEM_DATA.END_DATE, endDate2018)
		.where(SYSTEM_DATA.DOMAIN.in(-106))
		.and(SYSTEM_DATA.NAME.in(PORCENTAJE_IT, PORCENTAJE_IMS))
		.and(SYSTEM_DATA.START_DATE.eq(startDate2014))
		;

		InsertSetMoreStep<SystemDataRecord> insertCgcPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_CGC)
		.set(SYSTEM_DATA.END_DATE, (LocalDate) null)
		.set(SYSTEM_DATA.START_DATE, startDate2019)
		.set(SYSTEM_DATA.EXPRESSION, "4.70" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		InsertSetMoreStep<SystemDataRecord> insertCgcEPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_CGC_E)
		.set(SYSTEM_DATA.END_DATE, (LocalDate) null)
		.set(SYSTEM_DATA.START_DATE, startDate2019)
		.set(SYSTEM_DATA.EXPRESSION, "23.60" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		InsertSetMoreStep<SystemDataRecord> insertITPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_IT)
		.set(SYSTEM_DATA.END_DATE, (LocalDate) null)
		.set(SYSTEM_DATA.START_DATE, startDate2019)
		.set(SYSTEM_DATA.EXPRESSION, "0.80" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null)
		;

		InsertSetMoreStep<SystemDataRecord> insertIMSPercentage = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, -106)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_IMS)
		.set(SYSTEM_DATA.END_DATE, (LocalDate) null)
		.set(SYSTEM_DATA.START_DATE, startDate2019)
		.set(SYSTEM_DATA.EXPRESSION, "0.70" )
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null);

		dslContext.transaction(config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			close2018.execute();
			closeITIMS.execute();
			insertCgcPercentage.execute();
			insertCgcEPercentage.execute();
			insertITPercentage.execute();
			insertIMSPercentage.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
