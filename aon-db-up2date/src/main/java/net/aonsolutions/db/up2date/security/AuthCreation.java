package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;

public class AuthCreation implements Update {
	
//	#
//	# Structure for the `auth` table : 
//	#
//
//	CREATE TABLE `auth` (
//	  `id` BINARY(16) NOT NULL COMMENT 'Identificador unico',
//	  `email` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Identificador del Usuario (Email)',
//	  `password` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Contrasena del Usuario',
//	  PRIMARY KEY (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Autenticacion';
//
// 	ALTER TABLE `user` ADD `auth` binary(16) DEFAULT NULL COMMENT `uuid auth`;
	
	
	public static AuthCreation AUTH_CREATION = new AuthCreation();
	
	private AuthCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		System.out.println("[START]");
		System.out.println( "Creacion table AUTH" );

		String SQL = 
		"CREATE TABLE IF NOT EXISTS `auth` ("
			  + "`id` BINARY(16) NOT NULL COMMENT 'Identificador unico',"
			  + "`email` varchar(64) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Identificador del Usuario (Email)',"
			  + "`password` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Contrasena del Usuario',"
		  + "PRIMARY KEY (`id`),"
		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Autenticacion';"
		;	
		
		dslContext.execute(SQL);

		dslContext.alterTable(User.USER).addColumn("auth", SQLDataType.BINARY(16).nullable(true)).execute();
		System.out.println("[END]");
	}

}
