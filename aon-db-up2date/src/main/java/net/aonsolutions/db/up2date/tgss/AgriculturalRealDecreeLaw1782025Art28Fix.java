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

public class AgriculturalRealDecreeLaw1782025Art28Fix implements Update {

	private static final String ART_28_CORTA_DURACION = "ART_28_CORTA_DURACION";
	private static final String ART_151_CORTA_DURACION = "ART_151_CORTA_DURACION";
	public static AgriculturalRealDecreeLaw1782025Art28Fix AGRICULTURALREALDECREELAW1782025ART28FIX = new AgriculturalRealDecreeLaw1782025Art28Fix();

	
	private AgriculturalRealDecreeLaw1782025Art28Fix() {
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
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(-107))
		.and(SYSTEM_DATA.NAME.eq(ART_28_CORTA_DURACION))) > 0;

		if ( upgraded ) 
			return;

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			
			// Insert ART. 28. COT. ADICIONAL CONTRATOS CORTA DURACIÓN
			// Insert ART. 151. COT. ADICIONAL CONTRATOS CORTA DURACIÓN
			dslContext
			.insertInto(SYSTEM_DATA)
			.columns(
			SYSTEM_DATA.DOMAIN,
			SYSTEM_DATA.NAME,
			SYSTEM_DATA.START_DATE,
			SYSTEM_DATA.END_DATE,
			SYSTEM_DATA.EXPRESSION
			).select(DSL.select(
			DSL.inline(-107),
			SYSTEM_DATA.NAME,
			SYSTEM_DATA.START_DATE,
			SYSTEM_DATA.END_DATE,
			DSL.inline("FALSO()")
			)
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(0))
			.and(SYSTEM_DATA.NAME.in(ART_151_CORTA_DURACION, ART_28_CORTA_DURACION))
			).execute()
			;
			

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
