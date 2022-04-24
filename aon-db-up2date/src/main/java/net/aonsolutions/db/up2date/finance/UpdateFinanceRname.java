package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateFinanceRname implements Update {

	public static final UpdateFinanceRname UPDATE_FINANCE_RNAME = new UpdateFinanceRname();

	private UpdateFinanceRname() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		dslContext.select(FINANCE.ID, FINANCE.DOMAIN, FINANCE.REGISTRY)
			.from(FINANCE)
			.where(FINANCE.INVOICE.isNotNull())
			.and(FINANCE.RNAME.isNull())
			.and(FINANCE.REGISTRY.isNotNull()).fetch().stream().forEach(r1 -> {
				Integer financeId = r1.getValue(FINANCE.ID);
				Integer domainId = r1.getValue(FINANCE.DOMAIN);
				Integer registryId = r1.getValue(FINANCE.REGISTRY);
				
				String registryName = dslContext.select(REGISTRY.NAME).from(REGISTRY).where(REGISTRY.ID.eq(registryId))
						.fetch().stream().map(r2 -> r2.getValue(REGISTRY.NAME)).findFirst().orElse(null);
				
				dslContext.update(FINANCE)
					.set(FINANCE.RNAME, registryName)
					.where(FINANCE.DOMAIN.eq(domainId)).and(FINANCE.ID.eq(financeId))
					.execute();
			});
	}

}
