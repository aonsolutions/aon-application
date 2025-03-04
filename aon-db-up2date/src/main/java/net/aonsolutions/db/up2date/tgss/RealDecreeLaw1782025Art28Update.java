package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;
import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

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

public class RealDecreeLaw1782025Art28Update implements Update {

	private static final String ART_28_CORTA_DURACION = "ART_28_CORTA_DURACION";
	private static final String ART_151_CORTA_DURACION = "ART_151_CORTA_DURACION";
	private static final String CGC_E_TEMP = "CGC_E_TEMP";
	public static RealDecreeLaw1782025Art28Update REALDECREELAW1782025ART28UPDATE = new RealDecreeLaw1782025Art28Update();

	
	private RealDecreeLaw1782025Art28Update() {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		calendar.set(Calendar.DAY_OF_MONTH, 31);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2024);		
		Date end2024Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);		
		Date start2025Date = new Date(calendar.getTimeInMillis());

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq(ART_28_CORTA_DURACION))
		.and(SYSTEM_DATA.START_DATE.eq(start2025Date))) > 0;

		if ( upgraded ) 
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// Close ART. 151. COT. ADICIONAL CONTRATOS CORTA DURACIÓN 
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, end2024Date )
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq(CGC_E_TEMP))
			.and(SYSTEM_COST.END_DATE.isNull())
			.execute()
			;

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.END_DATE, end2024Date )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(ART_151_CORTA_DURACION))
			.and(SYSTEM_DATA.END_DATE.isNull())
			.execute()
			;
			
			// Insert ART. 28. COT. ADICIONAL CONTRATOS CORTA DURACIÓN
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME,ART_28_CORTA_DURACION)
			.set(SYSTEM_DATA.START_DATE, start2025Date)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_DATA.EXPRESSION,"DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30")
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)0)
			.set(SYSTEM_COST.CODE, CGC_E_TEMP)
			.set(SYSTEM_COST.START_DATE, start2025Date)
			.set(SYSTEM_COST.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_COST.DESCRIPTION, "ART. 28. COT. ADICIONAL CONTRATOS CORTA DURACI\u00F3N")
			.set(SYSTEM_COST.EXPRESSION, "(ART_28_CORTA_DURACION && FIN == FIN_CONTRATO)? 32.60 :HIDE()"
			)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
