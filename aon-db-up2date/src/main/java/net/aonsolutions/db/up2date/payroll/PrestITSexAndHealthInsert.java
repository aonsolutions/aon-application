package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemPayment.SYSTEM_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class PrestITSexAndHealthInsert implements Update {

	public static final PrestITSexAndHealthInsert PRESTITSEXANDHEALTHINSERT = new PrestITSexAndHealthInsert();
	
	private PrestITSexAndHealthInsert() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		Integer prestItConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PREST_IT"))
		.fetchOne(PAYMENT_CONCEPT.ID);
		

		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JUNE);
		calendar.set(Calendar.YEAR, 2023);
		
		Date june2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(0))
		.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
		.and(SYSTEM_PAYMENT.START_DATE.eq(june2023Date))
		) > 1;
		
		if ( upgraded ) 
		    return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, june2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_MENSTRUACION_1_20} DÍAS DE IT POR MENSTRUACIÓN DEL 1º AL 20º DÍA" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_MENSTRUACION_1_20 * BASE_REGULADORA * 0.60 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, june2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_MENSTRUACION_21} DÍAS DE IT POR MENSTRUACIÓN A PARTIR DE 21º DÍA" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_MENSTRUACION_21 * BASE_REGULADORA * 0.75 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, june2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_INTERRUPCION_EMBARAZO_1_20} DÍAS DE IT POR INTERRUPCIÓN DEL EMBARAZO DEL 1º AL 20º DÍA" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_INTERRUPCION_EMBARAZO_1_20 * BASE_REGULADORA * 0.60 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, june2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_INTERRUPCION_EMBARAZO_21} DÍAS DE IT POR INTERRUPCIÓN DEL EMBARAZO A PARTIR DE 21º DÍA" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_INTERRUPCION_EMBARAZO_21 * BASE_REGULADORA * 0.75 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, june2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_SEMANA_39_EMBARAZO_1_20} DÍAS DE IT POR SEMANA 39º DEL EMBARAZO DEL 1º AL 20º DÍA" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_SEMANA_39_EMBARAZO_1_20 * BASE_REGULADORA * 0.60 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, 0 )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, june2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_SEMANA_39_EMBARAZO_21} DÍAS DE IT POR SEMANA 39º DEL EMBARAZO A PARTIR DE 21º DÍA" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_SEMANA_39_EMBARAZO_21 * BASE_REGULADORA * 0.75 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.execute()
			;
			
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0 )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, june2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT POR MENSTRUACIÓN A CARGO DEL INSS" )
			.set(SYSTEM_COST.EXPRESSION, "NOMINA ? ( -1 * DIAS_MENSTRUACION_1_20 * BASE_REGULADORA * 0.60  ) : REMOVE()" )
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, 0 )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, june2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT POR MENSTRUACIÓN A CARGO DEL INSS" )
			.set(SYSTEM_COST.EXPRESSION, "NOMINA ? ( -1 * DIAS_MENSTRUACION_21 * BASE_REGULADORA * 0.75  ) : REMOVE()" )
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, 0 )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, june2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT POR INTERRUPCIÓN DEL EMBARAZO A CARGO DEL INSS" )
			.set(SYSTEM_COST.EXPRESSION, "NOMINA ? ( -1 * DIAS_INTERRUPCION_EMBARAZO_1_20 * BASE_REGULADORA * 0.60  ) : REMOVE()" )
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, 0 )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, june2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT POR INTERRUPCIÓN DEL EMBARAZO A CARGO DEL INSS" )
			.set(SYSTEM_COST.EXPRESSION, "NOMINA ? ( -1 * DIAS_INTERRUPCION_EMBARAZO_21 * BASE_REGULADORA * 0.75  ) : REMOVE()" )
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, 0 )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, june2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT POR SEMANA 39º DEL EMBARAZO A CARGO DEL INSS" )
			.set(SYSTEM_COST.EXPRESSION, "NOMINA ? ( -1 * DIAS_SEMANA_39_EMBARAZO_1_20 * BASE_REGULADORA * 0.60  ) : REMOVE()" )
			.newRecord()
			.set(SYSTEM_COST.DOMAIN, 0 )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, june2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT POR SEMANA 39º DEL EMBARAZO A CARGO DEL INSS" )
			.set(SYSTEM_COST.EXPRESSION, "NOMINA ? ( -1 * DIAS_SEMANA_39_EMBARAZO_21 * BASE_REGULADORA * 0.75  ) : REMOVE()" )			
			.execute();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
