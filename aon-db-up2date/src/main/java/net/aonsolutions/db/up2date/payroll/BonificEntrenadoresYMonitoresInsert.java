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

public class BonificEntrenadoresYMonitoresInsert implements Update {

	public static final BonificEntrenadoresYMonitoresInsert BONIFICENTRENADORESYMONITORESINSERT = new BonificEntrenadoresYMonitoresInsert();
	
	private BonificEntrenadoresYMonitoresInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.YEAR, 2024);
		Date firstDayOfJuly2024 = new Date(calendar.getTimeInMillis());

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.START_DATE.eq(firstDayOfJuly2024))
		.and(SYSTEM_PAYMENT.EXPRESSION.containsIgnoreCase("BONIFIC.ENTRENADORES/MONITORES"))
		) >= 1;
		
		
		if ( upgraded )
			return;
		
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");



			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte)1)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.START_DATE, firstDayOfJuly2024)
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			"if( CNO ~= '372[2-4]' && RLCE ~= '994[1-2]' ) { SELF.addBonus('BONIFIC.ENTRENADORES/MONITORES. LEY 7/2024','CGC_E'); } HIDE() "
			)
			.execute(); 
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
