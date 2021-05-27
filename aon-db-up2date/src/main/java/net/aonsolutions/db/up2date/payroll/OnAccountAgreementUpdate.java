package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.PaymentConcept;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class OnAccountAgreementUpdate implements Update {

	private static final String A_CUENTA_CONVENIO = "A_CUENTA_CONVENIO";
	public static OnAccountAgreementUpdate ONACCOUNTAGREEMENTUPDATE = new OnAccountAgreementUpdate();

	private OnAccountAgreementUpdate() {
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
		

		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq(A_CUENTA_CONVENIO))) >= 1;

		if (upgraded)
			return;

		InsertSetMoreStep<PaymentConceptRecord> insert = 
		dslContext
		.insertInto(PAYMENT_CONCEPT)
		.set(PAYMENT_CONCEPT.DOMAIN,0)
		.set(PAYMENT_CONCEPT.TYPE,(byte)1)
		.set(PAYMENT_CONCEPT.IRPF_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION,"_P")
		.set(PAYMENT_CONCEPT.CODE,A_CUENTA_CONVENIO)
		.set(PAYMENT_CONCEPT.DESCRIPTION,"A CUENTA DEL CONVENIO")
		.set(PAYMENT_CONCEPT.EXPRESSION,"A_CUENTA_CONVENIO(/*user*/0.00/**/)")
		;
		
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			insert.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
