package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TempPaymentInsert implements Update {

	public static final TempPaymentInsert TEMPPAYMENTINSERT = new TempPaymentInsert();
	
	private TempPaymentInsert() {
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
		.and(PAYMENT_CONCEPT.CODE.like("DEVENGO_TEMPORAL"))
		) > 0;
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			dslContext
			.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "DEVENGO_TEMPORAL")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, "")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "DEVENGO TEMPORAL FIJO")
			.newRecord()
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "DEVENGO_TEMPORAL")
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "_P")
			.set(PAYMENT_CONCEPT.EXPRESSION, "/*user*/0.00/**/ * DIAS_TRABAJADOS / DIAS_MES")
			.set(PAYMENT_CONCEPT.DESCRIPTION, "DEVENGO TEMPORAL MENSUAL")
			.execute()
			;
		});
	}

}
