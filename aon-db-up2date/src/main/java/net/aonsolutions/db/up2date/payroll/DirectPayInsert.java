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

public class DirectPayInsert implements Update {

	public static final DirectPayInsert DIRECTPAYINSERT = new DirectPayInsert();
	
	private DirectPayInsert() {
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
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGO_DIRECTO"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");


			int directPayConceptId = 
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "PAGO_DIRECTO")
			.returning()
			.fetchOne()
			.getId();


			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, directPayConceptId )
			.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
			.and(SYSTEM_PAYMENT.EXPRESSION.like("%_366%"))
			.execute()
			;
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
