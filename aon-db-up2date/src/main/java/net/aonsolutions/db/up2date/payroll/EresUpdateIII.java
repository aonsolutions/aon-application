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

public class EresUpdateIII implements Update {

	public static final EresUpdateIII ERESUPDATEIII = new EresUpdateIII();
	
	private EresUpdateIII() {
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
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.in("ERE", "ERE_FZA", "ERE_FZA_EXONERADO"))
			.fetchInto(PAYMENT_CONCEPT)
			.forEach(concept -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.EXPRESSION, String.format("/*read-only*/DIAS_%s * 0.00/**/",concept.getCode()))
				.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(concept.getId()))
				.execute(); 

			});
			;
		});
	}

}
