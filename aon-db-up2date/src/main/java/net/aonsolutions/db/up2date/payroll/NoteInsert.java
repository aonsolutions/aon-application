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

public class NoteInsert implements Update {

	public static final NoteInsert NOTEINSERT = new NoteInsert();
	
	private NoteInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("NOTA"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "INFO")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "INFO")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/0.00/**/")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "NOTA")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "NOTA")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/0.00/**/")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "ADVERTENCIA")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "AVISO")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*read-only*/0.00/**/")
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
