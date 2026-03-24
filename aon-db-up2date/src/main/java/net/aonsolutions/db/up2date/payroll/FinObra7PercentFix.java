package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class FinObra7PercentFix implements Update {

	public static final FinObra7PercentFix FINOBRA7PERCENTFIX = new FinObra7PercentFix();
	
	private FinObra7PercentFix() {
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
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.EXPRESSION.contains("TOTAL_PAGADO"))
		) > 0;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( config ->

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.EXPRESSION, 
			DSL.regexpReplaceFirst(SYSTEM_PAYMENT.EXPRESSION, 
			"/\\*user\\*/.*?/\\*\\*/", 
			"/*user*/(TOTAL_DEVENGADO + TOTAL_PAGADO) * 7.00/100.00/**/")) 
			.where(SYSTEM_PAYMENT.EXPRESSION.contains("CAUSA_INDEMNIZACION == FIN_OBRA"))
			.execute()
			
		);
	}

}
