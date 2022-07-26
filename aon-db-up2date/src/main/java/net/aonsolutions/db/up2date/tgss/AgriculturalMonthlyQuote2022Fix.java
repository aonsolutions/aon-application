package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;

import org.jooq.DSLContext;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.SQLDialect;
import org.jooq.UpdateConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.SystemDataRecord;

import net.aonsolutions.db.up2date.Update;

public class AgriculturalMonthlyQuote2022Fix implements Update {

	public static AgriculturalMonthlyQuote2022Fix AGRICULTURALMONTHLYQUOTE2022FIX = new AgriculturalMonthlyQuote2022Fix();

	private static final int DOMAIN = -107;
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		// Establish context
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		

		boolean upgraded =
		dslContext.fetchCount(
		dslContext.select()
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq("COTIZACION_MENSUAL"))
		.and(SYSTEM_DATA.EXPRESSION.contains("GRUPO_COTIZACION"))) > 0;

		if ( upgraded ) 
			return;		
		
		

		UpdateConditionStep<SystemDataRecord> updateMonthlyQuote =
		dslContext
		.update(SYSTEM_DATA)
		.set(SYSTEM_DATA.EXPRESSION, "GRUPO_COTIZACION; isdef MODELO_COTIZACION_AGRARIO ? MODELO_COTIZACION_AGRARIO != 2 : VERDADERO()")
		.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
		.and(SYSTEM_DATA.NAME.eq("COTIZACION_MENSUAL"))
		;
		

		dslContext.transaction( (config) -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			updateMonthlyQuote.execute();
			
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
		
	}

}
