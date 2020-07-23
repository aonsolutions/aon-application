package net.aonsolutions.db.up2date.security;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UserAppRoleCreation implements Update {

//	#
//	# Structure for the `user_app_role` table : 
//	#
//
//	CREATE TABLE `user_app_role` (
//		`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
//		`domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',
//		`app` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'App',
//		`user_id` int(4) NOT NULL DEFAULT 0 COMMENT 'Identificador del Usuario',
//		`role` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'role',
//		PRIMARY KEY (`id`),
//		UNIQUE KEY `IDX_UNQ_USER_APP_ROLE` (`domain`, `user_id`),
//		KEY `IDX_USER_APP_ROLE_DOMAIN` (`domain`),
//		KEY `IDX_USER_APP_ROLE_USER` (`user_id`),
//		CONSTRAINT `FK_USER_APP_ROLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//		CONSTRAINT `FK_USER_APP_ROLE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Rol del usuario en una aplicacion';


	public static UserAppRoleCreation USER_APP_ROLE_CREATION = new UserAppRoleCreation();

	private UserAppRoleCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `user_app_role`" );

		String SQL =
		"CREATE TABLE IF NOT EXISTS `user_app_role` ("
			  + "`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',"
			  + "`domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',"
			  + "`app` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'App',"
			  + "`user_id` int(4) NOT NULL DEFAULT 0 COMMENT 'Identificador del Usuario',"
			  + "`role` tinyint(2) NOT NULL DEFAULT '0' COMMENT 'role',"
		  + "PRIMARY KEY (`id`),"
		  + "UNIQUE KEY `IDX_UNQ_USER_APP_ROLE` (`domain`, `user_id`),"
		  + "KEY `IDX_USER_APP_ROLE_DOMAIN` (`domain`),"
		  + "KEY `IDX_USER_APP_ROLE_USER` (`user_id`),"
		  + "CONSTRAINT `FK_USER_APP_ROLE_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
		  + "CONSTRAINT `FK_USER_APP_ROLE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)"
		+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Rol del usuario en una aplicacion';"
		;

		dslContext.execute(SQL);

		System.out.println("[END]");
	}

}
