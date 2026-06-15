package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class PPEITUpdateIII implements Update {

	public static final PPEITUpdateIII PPE_IT_UPDATEIII = new PPEITUpdateIII();
	
	private PPEITUpdateIII() {
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
		.and(SYSTEM_COST.EXPRESSION.contains("isdef TOTAL_PPE && isdef DIAS_TRABAJADOS"))
		.fetch(SYSTEM_COST.ID)
		;

		boolean upgraded = !oldCost.isEmpty() ;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "/*read-only*/ ( isdef TOTAL_PPE && isdef DIAS_TRABAJADOS ) ? TOTAL_PPE : 0.00 /**/")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq("PPE_E"))
			.and(SYSTEM_COST.EXPRESSION.notContains("isdef TOTAL_PPE && isdef DIAS_TRABAJADOS"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
