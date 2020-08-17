package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.Auth;
import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;

public class AuthUpdate implements Update {

// 	ALTER TABLE `auth` ADD `name` varchar(64) DEFAULT NULL COMMENT 'Nombre del usuario.';
// 	ALTER TABLE `auth` ADD `surname` varchar(64) DEFAULT NULL COMMENT 'Apellidos del usuario.';
// 	ALTER TABLE `auth` ADD `document` varchar(16) DEFAULT NULL COMMENT 'Documento de identificacion.';
// 	ALTER TABLE `auth` ADD `phone` varchar(16) DEFAULT NULL COMMENT 'Número de telefono movil del usuario.';
//
// 	ALTER TABLE `user` ADD `shared` tinyint(1) DEFAULT NULL COMMENT 'Indica si el usuario es compartido o no.';


	public static AuthUpdate AUTH_UPDATE = new AuthUpdate();

	private AuthUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Modificar table AUTH & USER" );

		dslContext.alterTable(Auth.AUTH).addColumn("name", SQLDataType.VARCHAR(64).nullable(true)).execute();
		dslContext.alterTable(Auth.AUTH).addColumn("surname", SQLDataType.VARCHAR(64).nullable(true)).execute();
		dslContext.alterTable(Auth.AUTH).addColumn("document", SQLDataType.VARCHAR(16).nullable(true)).execute();
		dslContext.alterTable(Auth.AUTH).addColumn("phone", SQLDataType.VARCHAR(16).nullable(true)).execute();

		dslContext.alterTable(User.USER).addColumn("shared", SQLDataType.TINYINT.nullable(false).defaultValue((byte) 0)).execute();

		System.out.println("[END]");
	}

}
