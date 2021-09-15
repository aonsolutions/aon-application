package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class RealDecreeLaw182020Update implements Update {

	public static final RealDecreeLaw182020Update REALDECREELAW182020UPDATE = new RealDecreeLaw182020Update();

	
	private RealDecreeLaw182020Update() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2020);		
		LocalDate startDate2020 = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.MONTH, Calendar.APRIL);		
		LocalDate april30Date = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.MAY);		
		LocalDate may1Date = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 12);
		calendar.set(Calendar.MONTH, Calendar.MAY);		
		LocalDate may12Date = new Date(calendar.getTimeInMillis()).toLocalDate();

		calendar.set(Calendar.DAY_OF_MONTH, 13);
		LocalDate may13Date = new Date(calendar.getTimeInMillis()).toLocalDate();


		dslContext.transaction(config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, april30Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.END_DATE.eq(may12Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.START_DATE, may1Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.START_DATE.eq(may13Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.START_DATE, may1Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_REINCORPORACION"))
			.and(SYSTEM_DATA.START_DATE.eq(may13Date))
			.execute();
			
			dslContext
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("ERE_FZA_EXONERADO"))
			.fetchOptional(PAYMENT_CONCEPT.ID)
			.ifPresent(ereFzaConceptId -> 
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.START_DATE, may1Date)
				.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
				.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(ereFzaConceptId))
				.and(SYSTEM_PAYMENT.START_DATE.eq(may13Date))
				.execute()
			);
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
		
	}

}
