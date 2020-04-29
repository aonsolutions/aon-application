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

public class MejoraInsert implements Update {

	public static final MejoraInsert MEJORAINSERT = new MejoraInsert();
	
	private MejoraInsert() {
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
		.and(PAYMENT_CONCEPT.CODE.eq("MEJORA"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)0)
			.set(PAYMENT_CONCEPT.CODE, "MEJORA")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "MEJORAS PREST. SS. EXPDTE. REG. DE EMPLEO")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.returning()
			.fetchOne()
			.getId();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
