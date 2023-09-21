package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class MaxEmbargable4ProrratedFix implements Update {

	public static final MaxEmbargable4ProrratedFix MAXEMBARGABLE4PRORRATEDFIX = new MaxEmbargable4ProrratedFix();
	
	private MaxEmbargable4ProrratedFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.YEAR, 2000);
			
			Date start2000Date = new Date(calendar.getTimeInMillis());
			

			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "def(total_liquido){ "
					+ "_dias_trabajados = DIAS_TRABAJADOS / COEFICIENTE_PARCIALIDAD; "
					+ "(MODALIDAD_MENSUAL || !PAGAS_PRORRATEADAS)? "
					+ " MAX_EMBARGABLE_MES(total_liquido) : "
					+ " MAX_EMBARGABLE_DIA(total_liquido / _dias_trabajados) * _dias_trabajados "
					+ "}")
			.where(SYSTEM_DATA.NAME.eq("MAX_EMBARGABLE") )
			.and(SYSTEM_DATA.START_DATE.eq(start2000Date))

			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}
}
