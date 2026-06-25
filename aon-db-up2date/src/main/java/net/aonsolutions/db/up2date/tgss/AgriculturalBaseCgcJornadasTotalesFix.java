package net.aonsolutions.db.up2date.tgss;

import static com.esferalia.aon.jooq.tables.SystemData.SYSTEM_DATA;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AgriculturalBaseCgcJornadasTotalesFix implements Update {

	public static final AgriculturalBaseCgcJornadasTotalesFix AGRICULTURALBASECGCJORNADASTOTALESFIX = new AgriculturalBaseCgcJornadasTotalesFix();

	private static final int DOMAIN = -107;

	private AgriculturalBaseCgcJornadasTotalesFix() {}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);

		boolean upgraded = dslContext.fetchCount(
			dslContext.select()
			.from(SYSTEM_DATA)
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.like("BASE_CGC_%"))
			.and(SYSTEM_DATA.EXPRESSION.like("%JORNADAS_REALES_TOTALES%"))
		) > 0;

		if (upgraded)
			return;

		dslContext.transaction(config -> {

			dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");

			dslContext
			.update(SYSTEM_DATA)
			.set(SYSTEM_DATA.EXPRESSION,
				DSL.replace(SYSTEM_DATA.EXPRESSION, "JORNADAS_REALES", "JORNADAS_REALES_TOTALES"))
			.where(SYSTEM_DATA.DOMAIN.eq(DOMAIN))
			.and(SYSTEM_DATA.NAME.like("BASE_CGC_%"))
			.and(SYSTEM_DATA.EXPRESSION.like("%JORNADAS_REALES%"))
			.and(SYSTEM_DATA.EXPRESSION.notLike("%JORNADAS_REALES_TOTALES%"))
			.execute();

			dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");

		});
	}

}
