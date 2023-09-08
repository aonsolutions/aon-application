package net.aonsolutions.db.up2date.irpf;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;
import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;

import net.aonsolutions.db.up2date.Update;

public class IrpfDelaysUpdate implements Update {

    	private static final String  LIRPF_MSG = "\"<div>Atrasos que corresponda imputar a ejercicios anteriores, el porcentaje de retención será del 15 por ciento <a href='https://www.boe.es/buscar/act.php?id=BOE-A-2006-20764#a101' target='_blank' style=' color:blue'  > (art. 101.1 LIRPF)</a> </div><div>&nbsp;</div><div class='aon-text-right'><span class='aon-icon aon-icon-logo'/>aon Solutions</div>\""; 
    
    	private static final String  LIRPF_EXPRESSION = 
	"if  (  AMBITO('PORCENTAJE_IRPF') < SALARY && AÑO(INICIO_NOMINA) < AÑO(TODAY)) {"+ 
	" SELF.addVariable('PORCENTAJE_IRPF',15.00);" +
	" HIDE(LIRPF_MSG);"+
	"}  else {" +
	" HIDE();" +
	"}";
    	
    
	public static final IrpfDelaysUpdate IRPFDELAYSUPDATE = new IrpfDelaysUpdate();
	
	private IrpfDelaysUpdate() {
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
		
		Date startOf2010 = new Date(calendar.getTimeInMillis());

		// CLEAN OLD 
		DeleteConditionStep<SystemPaymentRecord> deleteLIrpfPayment = 
		dslContext
		.delete(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.SALARY_TYPE.eq((byte)3))
		.and(SYSTEM_PAYMENT.DESCRIPTION.eq("LIRPF"));

		DeleteConditionStep<SystemDataRecord> deleteLIrpfMsg = 
		dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("LIRPF_MSG"));

		InsertSetMoreStep<SystemPaymentRecord> insertLIrpfPayment = dslContext
		.insertInto(SYSTEM_PAYMENT)
		.set(SYSTEM_PAYMENT.DOMAIN, 0)
		.set(SYSTEM_PAYMENT.TYPE, (byte)0 )
		.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)3)
		.set(SYSTEM_PAYMENT.DESCRIPTION, "LIRPF")
		.set(SYSTEM_PAYMENT.START_DATE, startOf2010)
		.set(SYSTEM_PAYMENT.EXPRESSION, LIRPF_EXPRESSION )
		;

		InsertSetMoreStep<SystemDataRecord> insertLIrpfMsg = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.NAME, "LIRPF_MSG" )
		.set(SYSTEM_DATA.EXPRESSION, LIRPF_MSG )
		.set(SYSTEM_DATA.START_DATE, startOf2010)
		;


		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			deleteLIrpfMsg.execute();
			insertLIrpfMsg.execute();
			deleteLIrpfPayment.execute();
			insertLIrpfPayment.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
