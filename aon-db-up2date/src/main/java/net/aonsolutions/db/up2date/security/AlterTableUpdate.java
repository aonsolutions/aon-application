package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class AlterTableUpdate implements Update {

	public static AlterTableUpdate ALTER_TABLE_UPDATE = new AlterTableUpdate();

	private AlterTableUpdate() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		//
		
		System.out.println("[START]");
		System.out.println( "Creacion table `domain_app`" );

		String SQL1 = "alter table `domain_app` drop index IDX_UNQ_DOMAIN_APP;";
		String SQL2 = "alter table `user_app_role` drop index IDX_UNQ_USER_APP_ROLE;";

		dslContext.execute(SQL1);
		dslContext.execute(SQL2);

		System.out.println("[END]");
	}

}
