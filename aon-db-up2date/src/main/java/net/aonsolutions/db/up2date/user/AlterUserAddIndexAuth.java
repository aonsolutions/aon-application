package net.aonsolutions.db.up2date.user;

import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterUserAddIndexAuth implements Update {

	public static final AlterUserAddIndexAuth ALTER_USER_ADD_INDEX_AUTH = new AlterUserAddIndexAuth();

	private AlterUserAddIndexAuth() {
	}

	public void upgrade(Connection connection) {
		Settings settings;
		DSLContext dslContext;

		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		try {
			dslContext.createIndex("IDX_USER_AUTH").on(USER, USER.AUTH).execute();
		} catch (DataAccessException e) {
		}

	}

}
