package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.AgreementPayment.AGREEMENT_PAYMENT;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ContractPayment;

import net.aonsolutions.db.up2date.Update;

public class CRA0050ExcesoRefactor implements Update {

	private static final String EXENTO_KM = "EXENTO_KM";
	private static final String EXCESO_0_19_KMS_REGEXP = "\\s*EXCESO\\s*\\(\\s*0\\.19\\s*\\*\\s*KMS\\)\\s*";
	public static final CRA0050ExcesoRefactor CRA0050EXCESOREFACTOR = new CRA0050ExcesoRefactor();
	
	private CRA0050ExcesoRefactor() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date january12010Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 16);
		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.YEAR, 2023);

		Date july162023Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 17);

		Date july172023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = dslContext
			.fetchCount(dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.NAME.eq(EXENTO_KM))) > 0;

		if (upgraded)
		    return;


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, EXENTO_KM)
			.set(SYSTEM_DATA.EXPRESSION, "0.19")
			.set(SYSTEM_DATA.START_DATE, january12010Date)
			.set(SYSTEM_DATA.END_DATE, july162023Date)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, EXENTO_KM)
			.set(SYSTEM_DATA.EXPRESSION, "0.26")
			.set(SYSTEM_DATA.START_DATE, july172023Date)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(SYSTEM_DATA.END_DATE))
			.execute()
			;
			
			dslContext
			.update(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.IRPF_EXPRESSION, "EXCESO(EXENTO_KM * KMS)")
			.set(PAYMENT_CONCEPT.QUOTE_EXPRESSION, "EXCESO(EXENTO_KM * KMS)")
			.where(PAYMENT_CONCEPT.TYPE.eq((byte)50))
			.and(PAYMENT_CONCEPT.IRPF_EXPRESSION.likeRegex(EXCESO_0_19_KMS_REGEXP))
			.and(PAYMENT_CONCEPT.QUOTE_EXPRESSION.likeRegex(EXCESO_0_19_KMS_REGEXP))
			.execute()
			;
			
			dslContext
			.update(AGREEMENT_PAYMENT)
			.set(AGREEMENT_PAYMENT.IRPF_EXPRESSION, "EXCESO(EXENTO_KM * KMS)")
			.set(AGREEMENT_PAYMENT.QUOTE_EXPRESSION, "EXCESO(EXENTO_KM * KMS)")
			.where(AGREEMENT_PAYMENT.TYPE.eq((byte)50))
			.and(AGREEMENT_PAYMENT.IRPF_EXPRESSION.likeRegex(EXCESO_0_19_KMS_REGEXP))
			.and(AGREEMENT_PAYMENT.QUOTE_EXPRESSION.likeRegex(EXCESO_0_19_KMS_REGEXP))
			.execute()
			;

			dslContext
			.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION, "EXCESO(EXENTO_KM * KMS)")
			.where(CONTRACT_PAYMENT.TYPE.eq((byte)50))
			.and(CONTRACT_PAYMENT.QUOTE_EXPRESSION.likeRegex(EXCESO_0_19_KMS_REGEXP))
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
