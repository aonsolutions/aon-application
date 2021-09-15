package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class CRA0062Insert implements Update {

	public static final CRA0062Insert CRA0062INSERT = new CRA0062Insert();

	private CRA0062Insert() {
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
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded62 =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.TYPE.eq((byte)62))) >= 1;

		InsertSetMoreStep<PaymentConceptRecord> insertPayment62 = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)62)
		.set(PAYMENT_CONCEPT.EXPRESSION,"")
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		//.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, DSL.castNull(String.class))
		.set(PAYMENT_CONCEPT.DESCRIPTION,"GASTOS DE TELETRABAJO")
		;

		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			if ( !upgraded62 )
				insertPayment62.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
