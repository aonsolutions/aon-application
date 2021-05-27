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

public class RealDecreeLaw182020Update implements Update {

	public static RealDecreeLaw182020Update REALDECREELAW182020UPDATE = new RealDecreeLaw182020Update();

	
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
		Date _2020StartDate = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.MONTH, Calendar.APRIL);		
		Date _April30Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.MAY);		
		Date _May1Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 12);
		calendar.set(Calendar.MONTH, Calendar.MAY);		
		Date _May12Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 13);
		Date _May13Date = new Date(calendar.getTimeInMillis());


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, _April30Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.END_DATE.eq(_May12Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.START_DATE, _May1Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_EXONERADO"))
			.and(SYSTEM_DATA.START_DATE.eq(_May13Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.START_DATE, _May1Date)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq("PORCENTAJE_REINCORPORACION"))
			.and(SYSTEM_DATA.START_DATE.eq(_May13Date))
			.execute();
			
			dslContext
			.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("ERE_FZA_EXONERADO"))
			.fetchOptional(PAYMENT_CONCEPT.ID)
			.ifPresent(ereFzaConceptId -> {
				dslContext
				.update(SYSTEM_PAYMENT)
				.set(SYSTEM_PAYMENT.START_DATE, _May1Date)
				.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
				.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(ereFzaConceptId))
				.and(SYSTEM_PAYMENT.START_DATE.eq(_May13Date))
				.execute()
				;
			});
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
