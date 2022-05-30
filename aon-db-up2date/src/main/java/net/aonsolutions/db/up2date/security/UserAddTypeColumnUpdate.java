package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;


public class UserAddTypeColumnUpdate implements Update {

	public static final UserAddTypeColumnUpdate USER_ADD_TYPE_COLUMN_UPDATE = new UserAddTypeColumnUpdate();

	private static final Logger LOGGER  = Logger.getLogger(UserAddTypeColumnUpdate.class.getName());

	private UserAddTypeColumnUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info("Alter table `user`");

		try {
			dslContext.alterTable(User.USER).addColumn("type", SQLDataType.TINYINT.length(2).notNull().defaultValue((byte) 0)).after(User.USER.DOMAIN).execute();
			LOGGER.info("[table 'user' Update!]");
		} catch (DataAccessException e) {
			LOGGER.info("[table 'auth_device' NOT Update!] " + e.getMessage());
		}
		LOGGER.info("[END]");
	}
	
}
