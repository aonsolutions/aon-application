package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemCost.SYSTEM_COST;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.SystemCost;

import net.aonsolutions.db.up2date.Update;

public class OvertimeCostsFix implements Update {

	public static final OvertimeCostsFix OVERTIMECOSTSFIX = new OvertimeCostsFix();
	
	private OvertimeCostsFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		dslContext.transaction( config -> {
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.CODE, "ESTR_E")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.TYPE.eq((byte)4))
			.execute();
			
			dslContext
			.update(SYSTEM_COST)
			.set(SYSTEM_COST.CODE, "NESTR_E")
			.where(SYSTEM_COST.DOMAIN.eq(0))
			.and(SYSTEM_COST.TYPE.eq((byte)5))
			.execute();

		});
	}

}
