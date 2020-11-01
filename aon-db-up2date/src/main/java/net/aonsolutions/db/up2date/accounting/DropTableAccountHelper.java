package net.aonsolutions.db.up2date.accounting;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class DropTableAccountHelper implements Update {

	public static final DropTableAccountHelper DROP_TABLE_ACCOUNT_HELPER = new DropTableAccountHelper();

	private DropTableAccountHelper() {
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		try {
			dslContext.dropTable("account_helper").execute();
			System.out.println("[DROPPED!]");
		} catch (Throwable t) {
			System.out.println("[NOT DROPPED!] - " + t.getMessage());
		}
	}

}
