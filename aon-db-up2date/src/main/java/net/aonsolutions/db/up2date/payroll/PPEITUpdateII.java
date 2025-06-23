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

public class PPEITUpdateII implements Update {

	public static final PPEITUpdateII PPE_IT_UPDATEII = new PPEITUpdateII();
	
	private PPEITUpdateII() {
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
		.and(SYSTEM_COST.EXPRESSION.contains("DIAS_TRABAJADOS"))
		.fetch(SYSTEM_COST.ID)
		;

		boolean upgraded = !oldCost.isEmpty() ;
		
		if ( upgraded )
			return;
		

		dslContext.transaction( config -> {
			
			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.EXPRESSION, "/*read-only*/ isdef TOTAL_PPE ? SELF.addBonus('REDUCCIÓN APORTACIÓN EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO','isdef DIAS_TRABAJADOS ? TOTAL_PPE * PORCENTAJE_CGC_E / 100.00 : HIDE()'); TOTAL_PPE : HIDE() /**/")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.CODE.eq("PPE_E"))
			.and(SYSTEM_COST.EXPRESSION.notContains("DIAS_TRABAJADOS"))
			.execute();


			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		});
	}

}
