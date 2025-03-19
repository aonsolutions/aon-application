package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementExtra.AGREEMENT_EXTRA;
import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.AgreementExtra;

import net.aonsolutions.db.up2date.Update;

public class AgreementPaymentsMonthFix implements Update {
	
	public static final AgreementPaymentsMonthFix AGREEMENTPAYMENTSMONTHFIX = new AgreementPaymentsMonthFix();
	

	private AgreementPaymentsMonthFix() {
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
		
		dslContext.transaction( (config) -> {
			config.dsl()
			.update(AGREEMENT_PAYMENT
			.leftOuterJoin(AGREEMENT_EXTRA)
			.on(AGREEMENT_PAYMENT.ID.eq(AGREEMENT_EXTRA.AGREEMENT_PAYMENT)))
			.setNull(AGREEMENT_PAYMENT.MONTH)
			.where(AGREEMENT_EXTRA.ID.isNull())
			.and(AGREEMENT_PAYMENT.MONTH.eq((byte)0))
			.execute()	;
		});
	}

	
}
