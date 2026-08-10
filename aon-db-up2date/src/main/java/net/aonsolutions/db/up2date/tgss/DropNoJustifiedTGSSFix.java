package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class DropNoJustifiedTGSSFix implements Update {
	
	public static final DropNoJustifiedTGSSFix DROPNOJUSTIFIEDTGSSFIX = new DropNoJustifiedTGSSFix();
	
	private DropNoJustifiedTGSSFix() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("AUSENCIA"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			PaymentConceptRecord unpaidConcept = 
			dslContext.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("UNPAID"))
			.fetchOneInto(PaymentConceptRecord.class);
			
			PaymentConceptRecord dropConcept = 
					dslContext.insertInto(PAYMENT_CONCEPT)
					.set(PAYMENT_CONCEPT.DOMAIN, 0)
					.set(PAYMENT_CONCEPT.TYPE, (byte)1)
					.set(PAYMENT_CONCEPT.CODE, "AUSENCIA")
					.returning()
					.fetchOne();

			dslContext
			.update(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, dropConcept.getId())
			.where(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(unpaidConcept.getId()))
			.and(SYSTEM_PAYMENT.DESCRIPTION.like("DIAS DE AUSENCIA%"))
			.execute();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
