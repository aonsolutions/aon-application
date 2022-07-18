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

public class RealDecreeLaw322021Art151Fix implements Update {

	private static final String ART_151_CORTA_DURACION = "ART_151_CORTA_DURACION";
	private static final String CGC_E_TEMP = "CGC_E_TEMP";
	public static RealDecreeLaw322021Art151Fix REALDECREELAW322021ART151FIX = new RealDecreeLaw322021Art151Fix();

	
	private RealDecreeLaw322021Art151Fix() {
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
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,"(REGIMEN != ARTISTAS) && (REGIMEN != AGRARIO) && (REGIMEN != HOGAR) && (!INDEFINIDO) && (DIAS(FIN_CONTRATO,INICIO_CONTRATO) < 30)")
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.eq(ART_151_CORTA_DURACION))
			.execute()
			;


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
