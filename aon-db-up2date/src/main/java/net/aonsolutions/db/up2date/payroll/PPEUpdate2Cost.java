package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class PPEUpdate2Cost implements Update {

	public static final PPEUpdate2Cost PPE_IT_UPDATE2COST = new PPEUpdate2Cost();
	
	private PPEUpdate2Cost() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		List<Integer> oldCost =
		dslContext
		.select(SYSTEM_COST.ID)
		.from(SYSTEM_COST)
		.where(SYSTEM_COST.DOMAIN.eq(0))
		.and(SYSTEM_COST.CODE.eq("PPE_E"))
		.and(SYSTEM_COST.EXPRESSION.contains("addBonus"))
		.fetch(SYSTEM_COST.ID)
		;

		boolean upgraded = oldCost.isEmpty() ;
		
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
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "/*read-only*/ isdef TOTAL_PPE ? TOTAL_PPE : HIDE() /**/")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq("PPE_E"))
			.execute();

			dslContext
			.insertInto(SYSTEM_COST)
			.set(SYSTEM_COST.DOMAIN, 0)
			.set(SYSTEM_COST.TYPE, (byte)0)
			.set(SYSTEM_COST.CODE, "RED_PPE_E")
			.set(SYSTEM_COST.START_DATE, startOf2010)
			.set(SYSTEM_COST.DESCRIPTION, "REDUCCION APORTACIÓN PLAN PENSIONES")
			.set(SYSTEM_COST.EXPRESSION, "isdef DIAS_TRABAJADOS ? -1 *  TOTAL_PPE * PORCENTAJE_CGC_E / 100.00 : HIDE()")
			.execute();

			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
