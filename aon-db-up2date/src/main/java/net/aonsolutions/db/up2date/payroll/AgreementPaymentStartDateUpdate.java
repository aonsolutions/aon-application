package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;

import java.sql.Connection;
import java.util.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AgreementPaymentStartDateUpdate implements Update {
	
	public static final AgreementPaymentStartDateUpdate AGREEMENT_PAYMENT_START_DATE_UPDATE = new AgreementPaymentStartDateUpdate();

	private AgreementPaymentStartDateUpdate() {}

	@Override
	public void upgrade(Connection connection) {
		
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.transaction( config -> updateStartDateColumn(dslContext));
	}

	private void updateStartDateColumn(DSLContext dslContext) {
		// Update start_date columns
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.YEAR, 2000);
		java.sql.Date date = new java.sql.Date(cal.getTimeInMillis());

		try {
			Date now = new Date();
			int updated = dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.START_DATE, date)
			.where(AGREEMENT_PAYMENT.START_DATE.gt(date))
			.execute();
			
			long millis = (new Date()).getTime() - now.getTime();
			System.out.println("\tAgreementStartDateUpdate AGREEMENT_PAYMENT.START_DATE UPDATED! [ " + updated + " rows affected; " + (millis / 1000) + " sec.]");
		} catch (Throwable e) {
			System.out.println("\tAgreementStartDateUpdate AGREEMENT_PAYMENT.START_DATE NOT UPDATED!");
			e.printStackTrace();
		}
	}

	
}
