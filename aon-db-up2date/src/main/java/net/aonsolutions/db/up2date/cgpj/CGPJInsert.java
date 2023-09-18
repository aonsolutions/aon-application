package net.aonsolutions.db.up2date.cgpj;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemData;

import net.aonsolutions.db.up2date.Update;

public class CGPJInsert implements Update {

	public static final CGPJInsert CGPJINSERT = new CGPJInsert();
	
	private CGPJInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		
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
		.and(SYSTEM_PAYMENT.START_DATE.eq(startOf2010Date))
		.and(SYSTEM_PAYMENT.DESCRIPTION.eq("CGPJ"))
		) > 1;
		
		if ( upgraded ) 
		    return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext 
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN,0)
			.set(SYSTEM_DATA.START_DATE,startOf2010Date)
			.set(SYSTEM_DATA.NAME, "CGPJ_INDEMNIZACIONES_HELP")
			.set(SYSTEM_PAYMENT.EXPRESSION,"'<table><tbody><tr><td colspan=\\'2\\' class=\\'aon-bold aon-text-center\\' >C.G.P.J - Cálculo de indemnizaciones por extinción de contrato de trabajo:</td></tr>@foreach{value:values}<tr><td><span class=\\'aon-bold\\'>@{value.title}</span> @{value.expression}</td><td class=\\'aon-bold aon-text-right\\'>@{value.amount}</td></tr>@end{}<tr><td></td><td><span class=\\'aon-icon aon-icon-logo aon-padding-top aon-padding-bottom\\' />aon Solutions</td></tr></tbody></table>'")
			.execute()
			;

			dslContext 
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN,0)
			.set(SYSTEM_PAYMENT.TYPE, (byte) 0)
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte) 2)
			.set(SYSTEM_PAYMENT.START_DATE,startOf2010Date)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "CGPJ")
			.set(SYSTEM_PAYMENT.EXPRESSION,"CGPJ_INDEMNIZACIONES = CALCULO_INDEMNIZACIONES(INICIO_NOMINA, FIN_NOMINA, SALARIO_DIA);HIDE(EVAL_TEMPLATE(CGPJ_INDEMNIZACIONES,CGPJ_INDEMNIZACIONES_HELP))")
			.execute()
			;
			
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
