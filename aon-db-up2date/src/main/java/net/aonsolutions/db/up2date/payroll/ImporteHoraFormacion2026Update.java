package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

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

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class ImporteHoraFormacion2026Update implements Update {

	public static final ImporteHoraFormacion2026Update IMPORTEHORAFORMACION2026UPDATE = new ImporteHoraFormacion2026Update();

	private static final String IMPORTE_HORA_FORMACION_DISTANCIA = "IMPORTE_HORA_FORMACION_DISTANCIA";
	private static final String IMPORTE_HORA_FORMACION_PRESENCIAL = "IMPORTE_HORA_FORMACION_PRESEN";

	private ImporteHoraFormacion2026Update() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.YEAR, 2026);
		Date start2026Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.MONTH, Calendar.MARCH);
		calendar.set(Calendar.YEAR, 2026);
		Date prevEndDate = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(IMPORTE_HORA_FORMACION_DISTANCIA))
		.and(SYSTEM_DATA.START_DATE.eq(start2026Date))) > 0;

		if ( upgraded )
			return;

		// Close currently open values (END_DATE is null)

		UpdateConditionStep<SystemDataRecord> closeDistancia =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, prevEndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(IMPORTE_HORA_FORMACION_DISTANCIA))
		.and(SYSTEM_DATA.END_DATE.isNull());

		UpdateConditionStep<SystemDataRecord> closePresencial =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.END_DATE, prevEndDate)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(IMPORTE_HORA_FORMACION_PRESENCIAL))
		.and(SYSTEM_DATA.END_DATE.isNull());

		// Insert new values

		InsertSetMoreStep<SystemDataRecord> insertDistancia =
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, IMPORTE_HORA_FORMACION_DISTANCIA)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, start2026Date)
		.set(SYSTEM_DATA.EXPRESSION, "6.30")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null);

		InsertSetMoreStep<SystemDataRecord> insertPresencial =
		dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, IMPORTE_HORA_FORMACION_PRESENCIAL)
		.set(SYSTEM_DATA.END_DATE, (Date) null)
		.set(SYSTEM_DATA.START_DATE, start2026Date)
		.set(SYSTEM_DATA.EXPRESSION, "10.10")
		.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
		.set(SYSTEM_DATA.COMMENTS, (String) null);

		// DOMAIN = 0 , GENERAL

		dslContext.transaction( config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			closeDistancia.execute();
			closePresencial.execute();
			insertDistancia.execute();
			insertPresencial.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});

	}

}
