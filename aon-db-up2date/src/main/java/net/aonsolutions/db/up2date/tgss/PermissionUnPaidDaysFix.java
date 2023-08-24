package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;

import net.aonsolutions.db.up2date.Update;

public class PermissionUnPaidDaysFix implements Update {
	
	public static final PermissionUnPaidDaysFix PERMISSIONNOTPAIDDAYSFIX = new PermissionUnPaidDaysFix();
	
	private PermissionUnPaidDaysFix() {
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
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("UNPAID"))
		) >= 1;
		
		
		if ( upgraded )
			return;

		// START_DATE 01/01/2010
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date _2010StartDate = new Date(calendar.getTimeInMillis());
		
		
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.deleteFrom(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.DESCRIPTION.eq("PERMISO NO RETRIBUIDO"))
			.execute();
			
			PaymentConceptRecord unpaidConcept = 
			dslContext.insertInto(PAYMENT_CONCEPT)
			.set(PAYMENT_CONCEPT.DOMAIN, 0)
			.set(PAYMENT_CONCEPT.TYPE, (byte)1)
			.set(PAYMENT_CONCEPT.CODE, "UNPAID")
			.returning()
			.fetchOne();
			
			dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "PERMISO NO RETRIBUIDO")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/(CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO)? DIAS_INACTIVIDAD * 0.00 : __HIDE_ /**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "BASE_CGC_MIN")
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.MONTH, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, -2)
			.set(SYSTEM_PAYMENT.TYPE, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "PERMISO NO RETRIBUIDO")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/(CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO)? DIAS_INACTIVIDAD * 0.00 : __HIDE_ /**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "BASE_CGC_MIN")
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.MONTH, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
