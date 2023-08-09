package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FixBofFormTDistan implements Update {

	public static final FixBofFormTDistan FIXBOFFORMTDISTAN = new FixBofFormTDistan();
	
	private FixBofFormTDistan() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_DATA.ID)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("IMPORTE_HORA_FORMACION_DISTANCIA"))
		) >= 1;
		
		
		if ( upgraded )
			return;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		Date firstDayOfYear2010 = new Date(calendar.getTimeInMillis());
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.START_DATE, firstDayOfYear2010)
			.set(SYSTEM_DATA.NAME, "IMPORTE_HORA_FORMACION_DISTANCIA")
			.set(SYSTEM_DATA.EXPRESSION, "5.00"
			)
			.execute();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			"isdef HORAS_FORMACION_DISTANCIA ? "
			+ "SELF.addBonus('BONIF FORM. T.DISTAN','HORAS_FORMACION_DISTANCIA * IMPORTE_HORA_FORMACION_DISTANCIA');HIDE() "
			+ ": isdef HORAS_FORMACION_PRESENCIAL ? HIDE() : HIDE(HORAS_FORM_DISTAN_MSG)"
			)
			.where( SYSTEM_PAYMENT.EXPRESSION.like("%BONIF FORM. T.DISTAN%"))
			.execute(); 
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
