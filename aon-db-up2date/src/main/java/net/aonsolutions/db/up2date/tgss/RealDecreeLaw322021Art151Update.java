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

public class RealDecreeLaw322021Art151Update implements Update {

	private static final String ART_151_CORTA_DURACION = "ART_151_CORTA_DURACION";
	private static final String CGC_E_TEMP = "CGC_E_TEMP";
	public static RealDecreeLaw322021Art151Update REALDECREELAW322021ART151UPDATE = new RealDecreeLaw322021Art151Update();

	
	private RealDecreeLaw322021Art151Update() {
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
		calendar.set(Calendar.DAY_OF_MONTH, 30);
		calendar.set(Calendar.MONTH, Calendar.DECEMBER);
		calendar.set(Calendar.YEAR, 2021);		
		Date december302021Date = new Date(calendar.getTimeInMillis());

		calendar.set(Calendar.DAY_OF_MONTH, 30);
		Date december312021Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.END_DATE, december302021Date )
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq(CGC_E_TEMP))
			.and(SYSTEM_COST.END_DATE.isNull())
			.and(SYSTEM_COST.START_DATE.lt(december312021Date))
			.execute()
			;

			dslContext
			.delete(SYSTEM_COST)
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq(CGC_E_TEMP))
			.and(SYSTEM_COST.START_DATE.ge(december312021Date))
			.execute()
			;
			
			dslContext
			.delete(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(ART_151_CORTA_DURACION))
			.and(SYSTEM_DATA.START_DATE.ge(december312021Date))
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME,ART_151_CORTA_DURACION)
			.set(SYSTEM_DATA.START_DATE, december312021Date)
			.set(SYSTEM_DATA.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_DATA.EXPRESSION,"DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30")
			.execute()
			;

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)0)
			.set(SYSTEM_COST.CODE, CGC_E_TEMP)
			.set(SYSTEM_COST.START_DATE, december312021Date)
			.set(SYSTEM_COST.END_DATE, DSL.castNull(Date.class))
			.set(SYSTEM_COST.DESCRIPTION, "ART. 151. COT. ADICIONAL CONTRATOS CORTA DURACI\u00F3N")
			.set(SYSTEM_COST.EXPRESSION, "(ART_151_CORTA_DURACION && FIN == FIN_CONTRATO)? 26.57 :HIDE()"
			)
			.execute()
			;

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
