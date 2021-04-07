package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import net.aonsolutions.db.up2date.Update;


public class AuthDeviceUpdate implements Update {
//	#
//	# Structure for the `auth_device` table :
//	#
//	alter table `auth_device` change column `device_token` `device_token` varchar(255) null;

	public static AuthDeviceUpdate AUTH_DEVICE_UPDATE = new AuthDeviceUpdate();

	private AuthDeviceUpdate() {
		super();
	}
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL_5_7, settings);

		System.out.println("[START]");
		System.out.println( "Update table `auth_device`" );
		try {
			dslContext.alterTable(DSL.name("auth_device")).alterColumn(DSL.name("device_token")).set(SQLDataType.VARCHAR(255).nullable(true)).execute();
			dslContext.alterTable(DSL.name("auth_device")).addColumn("last_date", SQLDataType.TIMESTAMP.defaultValue(DSL.currentTimestamp() ) ).execute();
			System.out.println("[table 'auth_device' Update!]");
		} catch (Throwable t) {
			System.out.println("[table 'auth_device' NOT Update!] " + t.getMessage());
		}
		System.out.println("[END]");
	}
	
}
