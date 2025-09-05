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

public class RealDecreeLaw1782025Art28300Fix510 implements Update {

	private static final String ART_28_CORTA_DURACION = "ART_28_CORTA_DURACION";
	public static RealDecreeLaw1782025Art28300Fix510 REALDECREELAW1782025ART28300FIX = new RealDecreeLaw1782025Art28300Fix510();

	
	private RealDecreeLaw1782025Art28300Fix510() {
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2025);		
		Date start2025Date = new Date(calendar.getTimeInMillis());

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			// Skips contracts 510 510 DURACIÓN DETERMINADA, TIEMPO PARCIAL, INTERINIDAD, from ART. 28. COT. ADICIONAL CONTRATOS CORTA DURACIÓN , 
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,"DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30 && TC2 != '300' &&  TC2 != '510' ")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(ART_28_CORTA_DURACION))
			.and(SYSTEM_DATA.START_DATE.eq(start2025Date))
			.execute()
			;


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
