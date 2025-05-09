package net.aonsolutions.db.up2date.tgss;

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

public class HomePrestITInsert implements Update {

	private static final int DOMAIN = -106;

	public static final HomePrestITInsert HOMEPRESTITINSERT = new HomePrestITInsert();
	
	private HomePrestITInsert() {
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
		
		Integer pagoDirectoConceptId = 
		dslContext
		.select(PAYMENT_CONCEPT.ID)
		.from(PAYMENT_CONCEPT)
		.where(PAYMENT_CONCEPT.DOMAIN.eq(0))
		.and(PAYMENT_CONCEPT.CODE.eq("PAGO_DIRECTO"))
		.fetchOne(PAYMENT_CONCEPT.ID);

		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2023);
		
		Date start2023Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_PAYMENT.ID)
		.from(SYSTEM_PAYMENT)
		.where(SYSTEM_PAYMENT.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_PAYMENT.PAYMENT_CONCEPT.eq(prestItConceptId))
		.and(SYSTEM_PAYMENT.START_DATE.eq(start2023Date))
		) > 1;
		
		if ( upgraded ) 
		    return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.insertInto(SYSTEM_PAYMENT)
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_ENFERMEDAD_COMUN_1_3} DÍAS DE IT POR EC DEL 1º AL 3º DÍA " )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_ENFERMEDAD_COMUN_1_3 * 0.00/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, prestItConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_ENFERMEDAD_COMUN_4_8} DÍAS DE IT POR EC DEL 4º AL 8º DÍA " )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_ENFERMEDAD_COMUN_4_8 * BASE_REGULADORA * 0.60 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_ENFERMEDAD_COMUN_9} DÍAS DE IT POR EC A PARTIR DEL 9º DÍA PAGO DIRECTO" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_ENFERMEDAD_COMUN_9 * 0.00/**/" )
			
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_ENFERMEDAD_PROFESIONAL} DÍAS DE IT POR AT/EP PAGO DIRECTO" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_ENFERMEDAD_PROFESIONAL * 0.00/**/" )
			
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_MATERNIDAD} DÍAS DE IT POR MATERNIDAD" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_MATERNIDAD * 0.00/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_PATERNIDAD} DÍAS DE IT POR PATERNIDAD" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_PATERNIDAD * 0.00/**/" )

			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId)
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_MENSTRUACION} DÍAS DE IT POR MENSTRUACIÓN PAGO DIRECTO" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_MENSTRUACION * 0.00/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_INTERRUPCION_EMBARAZO} DÍAS DE IT POR INTERRUPCIÓN DEL EMBARAZO PAGO DIRECTO" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_INTERRUPCION_EMBARAZO * 0.00/**/" )
			.newRecord()
			.set(SYSTEM_PAYMENT.DOMAIN, DOMAIN )
			.set(SYSTEM_PAYMENT.SALARY_TYPE, (byte)0 )
			.set(SYSTEM_PAYMENT.START_DATE, start2023Date )
			.set(SYSTEM_PAYMENT.PAYMENT_CONCEPT, pagoDirectoConceptId )
			.set(SYSTEM_PAYMENT.DESCRIPTION, "@{DIAS_SEMANA_39_EMBARAZO} DÍAS DE IT POR SEMANA 39º DEL EMBARAZO PAGO DIRECTO" )
			.set(SYSTEM_PAYMENT.QUOTE_EXPRESSION, "DIAS_COTIZADOS * BASE_REGULADORA" )
			.set(SYSTEM_PAYMENT.EXPRESSION, "/*read-only*/DIAS_SEMANA_39_EMBARAZO * 0.00/**/" )
			.execute()
			;
			
			
			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, DOMAIN )
			.set(SYSTEM_COST.TYPE, (byte) 8 )
			.set(SYSTEM_COST.CODE, "ECSS_E" )
			.set(SYSTEM_COST.START_DATE, start2023Date )
			.set(SYSTEM_COST.DESCRIPTION, "PREST. IT PAGO DELEGADO" )
			.set(SYSTEM_COST.EXPRESSION, "REMOVE()" )
			.execute();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
