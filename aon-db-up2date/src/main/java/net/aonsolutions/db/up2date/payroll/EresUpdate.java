package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class EresUpdate implements Update {

	public static final EresUpdate ERESUPDATE = new EresUpdate();
	
	private EresUpdate() {
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
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.in("ERE", "ERE_FZA", "ERE_FZA_EXONERADO"))
			.fetchInto(PAYMENT_CONCEPT)
			.forEach(concept -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.DESCRIPTION, 
				DSL.concat(String.format("@{DIAS_%s} D\u00CDAS DE ", 
				concept.getCode()), SYSTEM_PAYMENT.DESCRIPTION)) 
				.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0 )
				.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(concept.getId()))
				.and(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)1))
				.execute(); 
			});
			;


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
