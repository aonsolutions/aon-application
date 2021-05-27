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
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class PagaExtraUpdate implements Update {

	public static final PagaExtraUpdate PAGAEXTRAUPDATE = new PagaExtraUpdate();
	
	private PagaExtraUpdate() {
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
			.set(PAYMENT_CONCEPT.EXPRESSION, "INPUT(\"/*user*/MENSUALIDAD/**/\",PAGA_EXTRA_HELP)")
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("PAGA_EXTRA"))
			.execute()
			;
		});
	}

}
