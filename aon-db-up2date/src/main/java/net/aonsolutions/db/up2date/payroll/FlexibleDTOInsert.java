package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class FlexibleDTOInsert implements Update {

	public static final FlexibleDTOInsert FLEXIBLEDTOINSERT = new FlexibleDTOInsert();
	
	
	private FlexibleDTOInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		// START_DATE 01/01/2010
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date startOf2010Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = 
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.innerJoin(PAYMENT_CONCEPT).on(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(PAYMENT_CONCEPT.ID))
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.like("DTO_FLEXIBLE"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			PaymentConceptRecord dtoFlexibleConcept = 
			dslContext.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("DTO_FLEXIBLE"))
			.fetchAnyInto(PAYMENT_CONCEPT);
			
			String flexibleSumExpression =
			dslContext
			.select(PAYMENT_CONCEPT.CODE)
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.like("FLEXIBLE%"))
			.groupBy(PAYMENT_CONCEPT.CODE)
			.fetchStreamInto(PAYMENT_CONCEPT)
			.map(PaymentConceptRecord::getCode)
			.map( code -> String.format("IFNDEF('%s',0)", code) )
			.collect(Collectors.joining("+"));
			
			dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, dtoFlexibleConcept.getId())
			.set(SYSTEM_PAYMENT.EXPRESSION, String.format("/*hideable*/TOTAL_DEVENGADO;DTO=(%s);(DTO > 0) ? -DTO : HIDE()", flexibleSumExpression ) )
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010Date)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
