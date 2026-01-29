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

public class FlexibleChecksDelete implements Update {

	public static final FlexibleChecksDelete FLEXIBLECHECKSDELETE = new FlexibleChecksDelete();
	
	
	private FlexibleChecksDelete() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		

		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.EXPRESSION.contains("MSG_CHECK_FLEXIBLE_"))
		) == 0;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.like("MSG_CHECK_FLEXIBLE_%"))
			.execute();
			
			dslContext
			.delete(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.EXPRESSION.contains("MSG_CHECK_FLEXIBLE_"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
