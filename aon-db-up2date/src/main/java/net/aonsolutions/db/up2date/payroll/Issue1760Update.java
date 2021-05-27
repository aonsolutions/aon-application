package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class Issue1760Update implements Update {

	public static final Issue1760Update ISSUE1760UPDATE = new Issue1760Update();
	
	private Issue1760Update() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( (config) -> {
			int updated = dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.where(SYSTEM_PAYMENT.EXPRESSION.contains("CAUSA_INDEMNIZACION == FIN_OBRA"))
			.or(SYSTEM_PAYMENT.EXPRESSION.contains("CAUSA_INDEMNIZACION == FIN_TEMPORAL"))
			.execute()
			;
			System.out.print("I.R.P.F 100% for " + updated + " payments ");
		});
	}

}
