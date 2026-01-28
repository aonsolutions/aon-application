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

public class FlexibleChecksInsert implements Update {

	public static final FlexibleChecksInsert FLEXIBLECHECKSINSERT = new FlexibleChecksInsert();
	
	
	private FlexibleChecksInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// START_DATE 01/01/2010
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date startOf2010Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_DATA.ID)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("MSG_CHECK_FLEXIBLE_COMIDA"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "MSG_CHECK_FLEXIBLE_COMIDA")
			.set(SYSTEM_DATA.EXPRESSION, "\"<div>Retribuci\u00F3n flexible tickets restaurante/comida tiene un l\u00EDmite diario de 11 euros (\"+DIAS_LABORABLES+\" d\u00EDas x 11 = \"+(DIAS_LABORABLES*11.00)+\"). La cuant\u00EDa restante s\u00ED tributar\u00E1</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\"")
			.set(SYSTEM_DATA.START_DATE, startOf2010Date)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "MSG_CHECK_FLEXIBLE_TRANSPORTE")
			.set(SYSTEM_DATA.EXPRESSION, "\"<div>Retribuci\u00F3n flexible transporte tiene un l\u00EDmite mensual de 136 euros. La cuant\u00EDa restante s\u00ED tributar\u00E1</div><div>&nbsp;</div><div class=\\'aon-text-right\\'><span class=\\'aon-icon aon-icon-logo\\' />aon Solutions</div>\"")
			.set(SYSTEM_DATA.START_DATE, startOf2010Date)
			.execute();
			
			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "SELF.addBonus('CHECK(FLEXIBLE_COMIDA <= 11 * DIAS_LABORABLES, MSG_CHECK_FLEXIBLE_COMIDA)');HIDE()" )
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010Date)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "SELF.addBonus('CHECK(FLEXIBLE_TRANSPORTE <= 136, MSG_CHECK_FLEXIBLE_TRANSPORTE)');HIDE()" )
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010Date)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
