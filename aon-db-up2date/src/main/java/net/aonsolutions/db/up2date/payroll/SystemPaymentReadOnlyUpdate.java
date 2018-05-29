package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;
import static org.jooq.impl.SQLDataType.VARCHAR;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class SystemPaymentReadOnlyUpdate implements Update {

	public static final SystemPaymentReadOnlyUpdate SYSTEMPAYMENTREADONLYUPDATE = new SystemPaymentReadOnlyUpdate();
	
	private SystemPaymentReadOnlyUpdate() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext
		.alterTable(SYSTEM_PAYMENT)
		.alter(SYSTEM_PAYMENT.EXPRESSION)
		.set(VARCHAR.length(256))
		.execute()
		;
		
		dslContext.transaction( (config) -> {
			

			int updated = dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, DSL.concat(DSL.concat("/*read-only*/", SYSTEM_PAYMENT.EXPRESSION), "/**/") )
			.where(SYSTEM_PAYMENT.EXPRESSION.isNotNull())
			.and(SYSTEM_PAYMENT.EXPRESSION.notLike("%/*read-only*/%"))
			.execute()
			;
			System.out.print("Set read-only: " + updated + " system payments ");
			
		});
	}

}
