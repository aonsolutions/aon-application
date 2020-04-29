package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class EresUpdateII implements Update {

	public static final EresUpdateII ERESUPDATE = new EresUpdateII();
	
	private EresUpdateII() {
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
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			dslContext
			.alterTable(SYSTEM_PAYMENT)
			.alterColumn(SYSTEM_PAYMENT.DESCRIPTION).set(SQLDataType.VARCHAR.length(256)).execute();

			dslContext
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.in("ERE", "ERE_FZA", "ERE_FZA_EXONERADO"))
			.fetchInto(PAYMENT_CONCEPT)
			.forEach(concept -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.DESCRIPTION, 
				DSL.replace(SYSTEM_PAYMENT.DESCRIPTION, 
				String.format("@{DIAS_%s} D\u00CDAS DE ",concept.getCode()), 
				String.format("@{ENTERO(DIAS_%1$s/COEFICIENTE_%1$s)} D\u00CDAS @if{ COEFICIENTE_%1$s < 1.00}AL @{ENTERO(100.00 * COEFICIENTE_%1$s)}%2$s@end{} DE ",concept.getCode(),"%") )) 
				.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(concept.getId()))
				.execute(); 

			});
			;
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
