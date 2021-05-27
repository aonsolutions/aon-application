package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class IndemnizacionDescriptionUpdate implements Update {

	public static final IndemnizacionDescriptionUpdate INDEMNIZACIONDESCRIPTIONUPDATE = new IndemnizacionDescriptionUpdate();
	
	private IndemnizacionDescriptionUpdate() {
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
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DESCRIPTION, "INDEMNIZACIONES POR DESPIDO O CESE ( @{SALARIO_DIA} \u20AC X @{20 * A\u00D1OS_TRABAJADOS} D\u00CDAS )" )  
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("INDEMNIZACION"))
			.execute();		
		});			
	}

}
