package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemDeduction;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemDeductionRecord;

import net.aonsolutions.db.up2date.Update;

public class RealDecreeLaw182020UpdateIV implements Update {

	public static RealDecreeLaw182020UpdateIV REALDECREELAW182020UPDATEIV = new RealDecreeLaw182020UpdateIV();

	
	private RealDecreeLaw182020UpdateIV() {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.YEAR, 2020);		

		calendar.set(Calendar.MONTH, Calendar.JULY);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date _July1Date = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		Date _July31Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.MONTH, Calendar.AUGUST);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date _August1Date = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		Date _August31Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date _September1Date = new Date(calendar.getTimeInMillis());
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		Date _September30Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.START_DATE.ge(_July1Date))
			.execute();
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_REINCORPORACION"))
			.and(SYSTEM_DATA.START_DATE.ge(_July1Date))
			.execute();

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, _July31Date)
			.set(SYSTEM_DATA.START_DATE, _July1Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 70.00 : 35.00 ")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, _August31Date)
			.set(SYSTEM_DATA.START_DATE, _August1Date)
			.set(SYSTEM_DATA.EXPRESSION,"ERE_TOTAL ? 60.00 : 35.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_EXONERADO")
			.set(SYSTEM_DATA.END_DATE, _September30Date)
			.set(SYSTEM_DATA.START_DATE, _September1Date)
			.set(SYSTEM_DATA.EXPRESSION,"35.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "PORCENTAJE_REINCORPORACION")
			.set(SYSTEM_DATA.END_DATE, _September30Date)
			.set(SYSTEM_DATA.START_DATE, _July1Date)
			.set(SYSTEM_DATA.EXPRESSION,"60.00")
			.set(SYSTEM_DATA.READ_ONLY, (byte) 1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
