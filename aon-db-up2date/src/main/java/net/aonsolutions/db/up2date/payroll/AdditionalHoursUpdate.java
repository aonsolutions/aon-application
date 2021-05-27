package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
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

public class AdditionalHoursUpdate implements Update {

	public static final AdditionalHoursUpdate ADDITIONALHOURSUPDATE = new AdditionalHoursUpdate();
	
	private AdditionalHoursUpdate() {
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
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/(HORAS_COMPLEMENTARIAS=FRACCIONAR(HORAS_COMPLEMENTARIAS)) * IMPORTE_HORA_COMPLEMENTARIA/**/")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.TYPE.in((byte)57,(byte)58))
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
