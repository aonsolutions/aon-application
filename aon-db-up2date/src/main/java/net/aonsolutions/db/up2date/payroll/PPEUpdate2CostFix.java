package net.aonsolutions.db.up2date.payroll;

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

import net.aonsolutions.db.up2date.Update;

public class PPEUpdate2CostFix implements Update {

	public static final PPEUpdate2CostFix PPEUPDATE2COSTFIX = new PPEUpdate2CostFix();
	
	private PPEUpdate2CostFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		
		
		boolean upgraded =
		dslContext.fetchCount(
		dslContext
		.select(SYSTEM_DATA.ID)
		.from(SYSTEM_DATA)
		.where(SYSTEM_DATA.DOMAIN.eq(0))
		.and(SYSTEM_DATA.NAME.eq("REDUCCION_APORTACION_EMPRESA_PPE"))
		) >= 1;
		
		if ( upgraded )
			return;
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.MONTH, Calendar.JANUARY);
		calendar.set(Calendar.YEAR, 2010);
		
		Date startOf2010 = new Date(calendar.getTimeInMillis());

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			//FORMACIÓN EN ALTERNANCIA
			dslContext
			.insertInto(SYSTEM_DATA)
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "FORMACION_EN_ALTERNANCIA")
			.set(SYSTEM_DATA.START_DATE, startOf2010)
			.set(SYSTEM_DATA.EXPRESSION, "['421':true,'521':false][TC2] != null")
			.newRecord()
			.set(SYSTEM_DATA.DOMAIN, 0)
			.set(SYSTEM_DATA.NAME, "REDUCCION_APORTACION_EMPRESA_PPE")
			.set(SYSTEM_DATA.START_DATE, startOf2010)
			.set(SYSTEM_DATA.EXPRESSION, "!FORMACION_EN_ALTERNANCIA")
			.execute();

			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "( isdef DIAS_TRABAJADOS  &&  REDUCCION_APORTACION_EMPRESA_PPE ) ? -1 *  TOTAL_PPE * PORCENTAJE_CGC_E / 100.00 : HIDE(READ)")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq("RED_PPE_E"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
