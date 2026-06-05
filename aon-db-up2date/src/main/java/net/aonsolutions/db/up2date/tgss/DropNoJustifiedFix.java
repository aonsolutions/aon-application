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

public class DropNoJustifiedFix implements Update {
	
	public static final DropNoJustifiedFix DROPNOJUSTIFIEDFIX = new DropNoJustifiedFix();
	
	private DropNoJustifiedFix() {
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
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.DESCRIPTION.startsWith("AUSENCIA NO JUSTIFICADA"))
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
		
		Date startOf2010 = new Date(calendar.getTimeInMillis());
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext.deleteFrom(SYSTEM_PAYMENT)
			.where(SYSTEM_PAYMENT.DESCRIPTION.eq("DIAS DE AUSENCIA"))
			.execute();
			
			PaymentConceptRecord unpaidConcept = 
			dslContext.select()
			.from(PAYMENT_CONCEPT)
			.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
			.and(PAYMENT_CONCEPT.CODE.eq("UNPAID"))
			.fetchOneInto(PaymentConceptRecord.class);
			
			dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "DIAS DE AUSENCIA@{FECHAS(\"' 'dd/MM' - 'dd/MM\")}")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/isdef CAUSA_AUSENCIA ? __HIDE_ : TOTAL_DEVENGADO; DIAS_AUSENCIA * 0.00/**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))")
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010)
			.set(SYSTEM_PAYMENT.MONTH, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, -2)
			.set(SYSTEM_PAYMENT.TYPE, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "DIAS DE AUSENCIA@{FECHAS(\"' 'dd/MM' - 'dd/MM\")}")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/isdef CAUSA_AUSENCIA ? __HIDE_ : TOTAL_DEVENGADO; DIAS_AUSENCIA * 0.00/**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))")
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010)
			.set(SYSTEM_PAYMENT.MONTH, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "AUSENCIA NO JUSTIFICADA@{FECHAS(\"' 'dd/MM' - 'dd/MM\")}")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/CAUSA_AUSENCIA == AUSENCIA_NO_JUSTIFICADA ? TOTAL_DEVENGADO; DIAS_AUSENCIA * 0.00 : __HIDE_/**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))")
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010)
			.set(SYSTEM_PAYMENT.MONTH, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, -2)
			.set(SYSTEM_PAYMENT.TYPE, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, unpaidConcept.getId())
			.set(SYSTEM_PAYMENT.DESCRIPTION, "AUSENCIA NO JUSTIFICADA@{FECHAS(\"' 'dd/MM' - 'dd/MM\")}")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/CAUSA_AUSENCIA == AUSENCIA_NO_JUSTIFICADA ? TOTAL_DEVENGADO; DIAS_AUSENCIA * 0.00 : __HIDE_/**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "/*fixBaseCgcMin*/MAX(0.00, BASE_CGC_MIN - (isdef BASE_CGC_BRUTA ? BASE_CGC_BRUTA : 0.00))")
			.set(SYSTEM_PAYMENT.START_DATE, startOf2010)
			.set(SYSTEM_PAYMENT.MONTH, DSL.castNull(Byte.class))
			.set(SYSTEM_PAYMENT.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0)
			.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
