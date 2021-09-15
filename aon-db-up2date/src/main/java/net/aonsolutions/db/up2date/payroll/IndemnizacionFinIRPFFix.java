package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class IndemnizacionFinIRPFFix implements Update {

	public static final IndemnizacionFinIRPFFix INDEMNIZACIONFINIRPFFIX = new IndemnizacionFinIRPFFix();
	
	private IndemnizacionFinIRPFFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);		
		

		
			
		dslContext.transaction(config -> 
			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P" )  
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)2))
			.and(SYSTEM_PAYMENT.EXPRESSION.likeIgnoreCase("%(CAUSA_INDEMNIZACION == FIN)%"))
			.execute()	
		);			
	}

}
