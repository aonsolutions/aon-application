package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class IfDaysInsert implements Update {
	
	public static final IfDaysInsert IFDAYSINSERT = new IfDaysInsert();
	
	private IfDaysInsert() {
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
		
		
		
		boolean upgraded = dslContext.fetchCount(
				dslContext.select().from(SYSTEM_PAYMENT)
					.where(SYSTEM_PAYMENT.DOMAIN.eq(-107))
					.and(SYSTEM_PAYMENT.START_DATE.eq(_2010StartDate))
					.and(SYSTEM_PAYMENT.DESCRIPTION.eq("PEONADAS/JORNADAS TEÓRICAS"))
				) == 1;

		// IF ALREADY EXISTS
				
		if ( upgraded )
			return;
		
		// DOMAIN = 0, PERMISSION_NOT_PAID_DAYS
		
		InsertSetMoreStep<SystemPaymentRecord> insertIfDaysSystemPayment = dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, -107)
			.set(SYSTEM_PAYMENT.TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, (Integer) null)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "PEONADAS/JORNADAS TEÓRICAS")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/JORNADAS_TEORICAS*0.00/**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "JORNADAS_TEORICAS * BASE_REGULADORA")
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.MONTH, (Byte) null)
			.set(SYSTEM_PAYMENT.END_DATE, (Date) null)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0);
			
		
		// DISABLED FOREING_KEY FOR INSERT
		
		
		UpdateConditionStep<ContractDataRecord> upateContractData = 
		dslContext.update(CONTRACT_DATA)
		.set(CONTRACT_DATA.NAME, "JORNADAS_REALES")
		.where(CONTRACT_DATA.NAME.eq("PEONADAS"))
		;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			upateContractData.execute();
			insertIfDaysSystemPayment.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
