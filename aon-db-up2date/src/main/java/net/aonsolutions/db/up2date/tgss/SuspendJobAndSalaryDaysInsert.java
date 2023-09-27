package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
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

import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class SuspendJobAndSalaryDaysInsert implements Update {
	
	public static final SuspendJobAndSalaryDaysInsert SUSPENDJOBANDSALARYDAYSINSERT = new SuspendJobAndSalaryDaysInsert();
	
	private SuspendJobAndSalaryDaysInsert() {
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
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.EXPRESSION.containsIgnoreCase("SUSPENSION_EMPLEO_SUELDO"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			PaymentConceptRecord unpaidConcept = 
			dslContext.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("UNPAID"))
			.fetchAnyInto(PAYMENT_CONCEPT);

			dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "SUSPENSIÓN DE EMPLEO Y SUELDO")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/(CAUSA_INACTIVIDAD == SUSPENSION_EMPLEO_SUELDO)? DIAS_INACTIVIDAD * 0.00 : __HIDE_ /**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010Date)
			.set(SYSTEM_PAYMENT.MONTH, (Byte) null)
			.set(SYSTEM_PAYMENT.END_DATE, (Date) null)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.execute();
				
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
