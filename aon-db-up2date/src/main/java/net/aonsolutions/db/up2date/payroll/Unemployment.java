package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.PaymentConcept.PAYMENT_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.DeleteConditionStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemData;
import com.esferalia.aon.jooq.tables.SystemPayment;
import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class Unemployment implements Update {

	public static final Unemployment UNEMPLOYMENT = new Unemployment();

	private static final String PORCENTAJE_DESEMPLEO = "PORCENTAJE_DESMPL";
	private static final String PORCENTAJE_DESEMPLEO_E = "PORCENTAJE_DESMPL_E";
	
	private Unemployment() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		boolean upgraded  =
		dslContext.fetchCount(
		dslContext
		.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESEMPLEO))
		) == 1
		;
		if ( upgraded )
			upgraded  =
			dslContext.fetchCount(
			dslContext
			.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESEMPLEO_E))
			) == 1
			;
		
		if ( upgraded )
			return;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2018);
		
		Date _2018StartDate = new Date(calendar.getTimeInMillis());

		
		DeleteConditionStep<SystemDataRecord> delete = 
		dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESEMPLEO))
		;
		
		DeleteConditionStep<SystemDataRecord> deleteE = 
		dslContext
		.delete(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_DESEMPLEO_E))
		;
		
		InsertSetMoreStep<SystemDataRecord> insert = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_DESEMPLEO)
		.set(SYSTEM_DATA.EXPRESSION, 
		"[ "
		+"\"100\": 1.55,"
		+"\"109\": 1.55,"
		+"\"130\": 1.55,"
		+"\"139\": 1.55,"
		+"\"150\": 1.55,"
		+"\"189\": 1.55,"
		
		+"\"200\": 1.55,"
		+"\"209\": 1.55,"
		+"\"230\": 1.55,"
		+"\"239\": 1.55,"
		+"\"250\": 1.55,"
		+"\"289\": 1.55,"
		
		+"\"300\": 1.55,"
		+"\"309\": 1.55,"
		+"\"330\": 1.55,"
		+"\"339\": 1.55,"
		+"\"350\": 1.55,"
		+"\"389\": 1.55,"
		
		+"\"401\": 1.60,"
		+"\"402\": 1.60,"
		+"\"403\": 1.60,"
		+"\"408\": 1.60,"
		+"\"410\": 1.55," // Interinidad
		+"\"418\": 1.55," // Interinidad
		+"\"420\": 1.55," // Prácticas
		+"\"421\": 1.60,"
		+"\"430\": 1.55," // Minusválidos
		+"\"441\": 1.55," // Relevo
		+"\"450\": 1.60,"
		+"\"452\": 1.60,"
		
		+"\"501\": 1.60,"
		+"\"502\": 1.60,"
		+"\"503\": 1.60,"
		+"\"508\": 1.60,"
		+"\"510\": 1.55," // Interinidad
		+"\"518\": 1.55," // Interinidad
		+"\"520\": 1.55," // Prácticas
		+"\"530\": 1.55," // Minusválidos
		+"\"540\": 1.60,"
		+"\"541\": 1.55," // Relevo
		+"\"550\": 1.60,"
		+"\"552\": 1.60," 

		+"\"970\": 1.60,"
		+"\"980\": 1.60,"
		+"\"990\": 1.60"
		+" ][TC2]"
		)
		;

		InsertSetMoreStep<SystemDataRecord> insertE = dslContext
		.insertInto(SYSTEM_DATA)
		.set(SYSTEM_DATA.DOMAIN, 0)
		.set(SYSTEM_DATA.START_DATE, _2018StartDate)
		.set(SYSTEM_DATA.NAME, PORCENTAJE_DESEMPLEO_E)
		.set(SYSTEM_DATA.EXPRESSION, 
		"[ "
		+"\"100\": 5.50,"
		+"\"109\": 5.50,"
		+"\"130\": 5.50,"
		+"\"139\": 5.50,"
		+"\"150\": 5.50,"
		+"\"189\": 5.50,"
		
		+"\"200\": 5.50,"
		+"\"209\": 5.50,"
		+"\"230\": 5.50,"
		+"\"239\": 5.50,"
		+"\"250\": 5.50,"
		+"\"289\": 5.50,"
		
		+"\"300\": 5.50,"
		+"\"309\": 5.50,"
		+"\"330\": 5.50,"
		+"\"339\": 5.50,"
		+"\"350\": 5.50,"
		+"\"389\": 5.50,"
		
		+"\"401\": 6.70,"
		+"\"402\": 6.70,"
		+"\"403\": 6.70,"
		+"\"408\": 6.70,"
		+"\"410\": 5.50," // Interinidad
		+"\"418\": 5.50," // Interinidad
		+"\"420\": 5.50," // Prácticas
		+"\"421\": 6.70,"
		+"\"430\": 5.50," // Minusválidos
		+"\"441\": 5.50," // Relevo
		+"\"450\": 6.70,"
		+"\"452\": 6.70,"
		
		+"\"501\": 6.70,"
		+"\"502\": 6.70,"
		+"\"503\": 6.70,"
		+"\"508\": 6.70,"
		+"\"510\": 5.50," // Interinidad
		+"\"518\": 5.50," // Interinidad
		+"\"520\": 5.50," // Prácticas
		+"\"530\": 5.50," // Minusválidos
		+"\"540\": 6.70,"
		+"\"541\": 5.50," // Relevo
		+"\"550\": 6.70,"
		+"\"552\": 6.70," 

		+"\"970\": 6.70,"
		+"\"980\": 6.70,"
		+"\"990\": 6.70"
		+" ][TC2]"
		)
		;

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			delete.execute();
			insert.execute();
			deleteE.execute();
			insertE.execute();
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
