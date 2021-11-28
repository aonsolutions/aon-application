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

public class BofFormYTutoriaInsert implements Update {

	public static final BofFormYTutoriaInsert BOFFORMYTUTORIAINSERT = new BofFormYTutoriaInsert();
	
	private BofFormYTutoriaInsert() {
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
		.and(SYSTEM_DATA.NAME.eq("HORAS_TUTORIA_MSG"))
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
			.set(SYSTEM_DATA.NAME, "HORAS_TUTORIA_MSG")
			.set(SYSTEM_DATA.EXPRESSION, 
			"\"<div>Para completar la <b>BONIFICACI\u00D3N TUTORIA</b> es necesario indicar las horas de tutoria ( HORAS_TUTORIA ).</div>"
			+"<div>&nbsp;</div>"
			+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\""
			)
			.execute();

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.START_DATE, firstDayOfYear2010)
			.set(SYSTEM_DATA.NAME, "HORAS_FORM_DISTAN_MSG")
			.set(SYSTEM_DATA.EXPRESSION, 
			"\"<div>Para completar la <b>BONIF FORM. T.DISTAN</b> es necesario indicar las horas de formaci\u00f3n ( HORAS_FORMACION_DISTANCIA ).</div>"
			+"<div>&nbsp;</div>"
			+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\""
			)
			.execute();

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.START_DATE, firstDayOfYear2010)
			.set(SYSTEM_DATA.NAME, "HORAS_FORM_PRESEN_MSG")
			.set(SYSTEM_DATA.EXPRESSION, 
			"\"<div>Para completar la <b>BONIF FORM. T.PRESEN</b> es necesario indicar las horas de formaci\u00f3n ( HORAS_FORMACION_PRESENCIAL ).</div>"
			+"<div>&nbsp;</div>"
			+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\""
			)
			.execute();

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte)1)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.START_DATE, firstDayOfYear2010)
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			"BONIFICACION_FORMACION_CONTINUA; "
			+ "isdef HORAS_FORMACION_DISTANCIA ? "
			+ "SELF.addBonus('BONIF FORM. T.DISTAN','HORAS_FORMACION_DISTANCIA; BONIFICACION_FORMACION_CONTINUA');HIDE() "
			+ ": isdef HORAS_FORMACION_PRESENCIAL ? HIDE() : HIDE(HORAS_FORM_DISTAN_MSG)"
			)
			.execute(); 
			
			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte)1)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.START_DATE, firstDayOfYear2010)
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			"BONIFICACION_FORMACION_CONTINUA; "
			+ "isdef HORAS_FORMACION_PRESENCIAL ? "
			+ "SELF.addBonus('BONIF FORM. T.PRESEN','HORAS_FORMACION_PRESENCIAL; BONIFICACION_FORMACION_CONTINUA');HIDE() "
			+ ": isdef HORAS_FORMACION_DISTANCIA ? HIDE() : HIDE(HORAS_FORM_PRESEN_MSG)"
			)
			.execute(); 

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte)1)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.START_DATE, firstDayOfYear2010)
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			"BONIFICACION_TUTORIA; "
			+ "isdef HORAS_TUTORIA ? "
			+ "SELF.addBonus('BONIFICACI\u00D3N TUTORIA','HORAS_TUTORIA; BONIFICACION_TUTORIA');HIDE() "
			+ ": HIDE(HORAS_TUTORIA_MSG)"
			)
			.execute(); 

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
