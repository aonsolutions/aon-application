package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class MEISolidarity2026RDL162025Fix implements Update {

	private static final String GRUPO_COTIZACION = "GRUPO_COTIZACION";

	private static final String PORCENTAJE_MEI = "PORCENTAJE_MEI";
	
	private static final String PORCENTAJE_MEI_E = "PORCENTAJE_MEI_E";

	private static final String PORCENTAJE_SOLIDARIDAD_I = "PORCENTAJE_SOLIDARIDAD_I";
	private static final String PORCENTAJE_SOLIDARIDAD_II = "PORCENTAJE_SOLIDARIDAD_II";
	private static final String PORCENTAJE_SOLIDARIDAD_III = "PORCENTAJE_SOLIDARIDAD_III";
	
	private static final String PORCENTAJE_SOLIDARIDAD_I_E = "PORCENTAJE_SOLIDARIDAD_I_E";
	private static final String PORCENTAJE_SOLIDARIDAD_II_E = "PORCENTAJE_SOLIDARIDAD_II_E";
	private static final String PORCENTAJE_SOLIDARIDAD_III_E = "PORCENTAJE_SOLIDARIDAD_III_E";

	private static final String APLICAR_RLD_162025 = "APLICAR_RLD_162025";

	public static final MEISolidarity2026RDL162025Fix MEISOLIDARITYRDL162025FIX = new MEISolidarity2026RDL162025Fix();


	private MEISolidarity2026RDL162025Fix() {
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
		
		// START_DATE 01/01/2025
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2026);
		
		Date startOf2026Date = new Date(calendar.getTimeInMillis());

		boolean upgraded = 
		dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.and(SYSTEM_DATA.NAME.eq(APLICAR_RLD_162025))
			) >= 1;

		// IF ALREADY EXISTS
		if ( upgraded ) 
			return;
		
		
		
		dslContext.transaction( config -> {
			
			// DISABLED FOREING_KEY FOR INSERT
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			String databaseName = dslContext.select(DSL.field("DATABASE()", String.class)).fetchOneInto(String.class);
			
			boolean isGrupoAyudatDB = "grupo-ayudat-aonsolutions-net".equalsIgnoreCase(databaseName);
			
			dslContext.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, APLICAR_RLD_162025)
			.set(SYSTEM_DATA.END_DATE, (Date) null)
			.set(SYSTEM_DATA.START_DATE, startOf2026Date)
			.set(SYSTEM_DATA.READ_ONLY, (byte)1)
			.set(SYSTEM_DATA.COMMENTS, (String) null)
			.set(SYSTEM_DATA.EXPRESSION,  isGrupoAyudatDB ? "FALSO()" : "VERDADERO()" )
			.execute();
			
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 0.15 : 0.13 ", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_MEI))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 0.75 : 0.67", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_MEI_E))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 0.19 : 0.15",GRUPO_COTIZACION,  APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_SOLIDARIDAD_I))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 0.96 : 0.77", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_SOLIDARIDAD_I_E))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 0.21 : 0.17", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_SOLIDARIDAD_II))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 1.04 : 0.83", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_SOLIDARIDAD_II_E))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 0.24 : 0.19", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_SOLIDARIDAD_III))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, String.format("%s; %s ? 1.22 : 0.98", GRUPO_COTIZACION, APLICAR_RLD_162025) )
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(PORCENTAJE_SOLIDARIDAD_III_E))
			.and(SYSTEM_DATA.START_DATE.eq(startOf2026Date))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
