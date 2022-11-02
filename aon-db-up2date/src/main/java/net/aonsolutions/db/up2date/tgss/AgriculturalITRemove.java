package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AgriculturalITRemove implements Update {

	public static final AgriculturalITRemove AGRICULTURALITREMOVE = new AgriculturalITRemove();
	
	private AgriculturalITRemove() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		List<Integer> itConceptIds = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.in("PREST_IT", "MTNAD", "PAGO_DIRECTO"))
		.fetch(PAYMENT_CONCEPT.ID);
		
		
		boolean	upgraded =
				dslContext.fetchCount(
				dslContext.select()
				.from(SYSTEM_PAYMENT)
				.where(SYSTEM_PAYMENT.DOMAIN.eq(-107))
				.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(itConceptIds))
				) == 0;
		
		if ( upgraded )
			return;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.DOMAIN.eq(-107))
			.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.in(itConceptIds))
			.execute()
			;


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
