package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
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
import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class PermissionNotPaidDaysInsert implements Update {
	
	public static final PermissionNotPaidDaysInsert PERMISSIONNOTPAIDDAYSINSERT = new PermissionNotPaidDaysInsert();
	
	private PermissionNotPaidDaysInsert() {
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
		
		
		

		// IF ALREADY EXISTS
		DeleteConditionStep<SystemPaymentRecord> deletePermissionNonPaidSystemPayment = dslContext.deleteFrom(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.DESCRIPTION.eq("PERMISO NO RETRIBUIDO"));
		
		// DOMAIN = 0, PERMISSION_NOT_PAID_DAYS
		
		InsertSetMoreStep<SystemPaymentRecord> insertPermissionNonPaidSystemPayment = dslContext.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0)
			.set(SYSTEM_PAYMENT.TYPE, (byte) 1)
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, (Integer) null)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "PERMISO NO RETRIBUIDO")
			.set(SYSTEM_PAYMENT.DESCRIPTION_DECORABLE, (byte) 0)
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read_only*/(CAUSA_INACTIVIDAD == PERMISO_NO_RETRIBUIDO)? DIAS_INACTIVIDAD * 0.00 : __HIDE_ /**/")
			.set(SYSTEM_PAYMENT.IRPF_EXPRESSION, "_P")
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "BASE_CGC_MIN")
			.set(SYSTEM_PAYMENT.START_DATE, _2010StartDate)
			.set(SYSTEM_PAYMENT.MONTH, (Byte) null)
			.set(SYSTEM_PAYMENT.END_DATE, (Date) null)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 0);
			
		
		// DISABLED FOREING_KEY FOR INSERT
		
		
		UpdateConditionStep<SystemPaymentRecord> upateDropDaysSystemPayment = 
		dslContext.update(SYSTEM_PAYMENT)
		.set(SYSTEM_PAYMENT.TYPE, (byte) 1)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.DESCRIPTION.eq("DIAS DE AUSENCIA"))
		;

		UpdateConditionStep<ContractDataRecord> upateContractData = 
		dslContext.update(CONTRACT_DATA)
		.set(CONTRACT_DATA.EXPRESSION, "PERMISO_NO_RETRIBUIDO")
		.where(CONTRACT_DATA.NAME.eq("CAUSA_INACTIVIDAD"))
		.and(CONTRACT_DATA.EXPRESSION.eq("Permiso no Retribuido"))
		;
		
		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			upateContractData.execute();
			upateDropDaysSystemPayment.execute();
			deletePermissionNonPaidSystemPayment.execute();
			insertPermissionNonPaidSystemPayment.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
