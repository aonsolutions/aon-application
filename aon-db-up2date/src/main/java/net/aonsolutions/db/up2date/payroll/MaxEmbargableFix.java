package net.aonsolutions.db.up2date.payroll;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class MaxEmbargableFix implements Update {

	public static final MaxEmbargableFix MAXEMBARGABLEFIX = new MaxEmbargableFix();
	
	private MaxEmbargableFix() {
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings ; 
		DSLContext dslContext;
		
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		
		dslContext.transaction( (config) -> {
			
			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION, "def(total_liquido){ "
					+ "_smi = ( PAGAS_PRORRATEADAS ? SMI * 14 /12 : SMI);"
					+ " MAX(((total_liquido - _smi) * 0.30),0)"
					+ " + MAX(((total_liquido - 2 * _smi ) * 0.20),0)"
					+ " + MAX((( total_liquido - 3 * _smi ) * 0.10),0)"
					+ " + MAX((( total_liquido - 4 * _smi ) * 0.15),0)"
					+ " + MAX((( total_liquido - 5 * _smi ) * 0.15),0)"
					+ "}")
			.where(SYSTEM_DATA.NAME.eq("MAX_EMBARGABLE"))
			.and(SYSTEM_DATA.DOMAIN.eq(0))
			.execute();
			
		});
	}

}
