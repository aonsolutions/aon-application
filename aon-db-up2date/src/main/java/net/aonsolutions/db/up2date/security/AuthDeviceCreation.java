package net.aonsolutions.db.up2date.security;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;


public class AuthDeviceCreation implements Update {
	
	/**
	#
	# Structure for the `auth_device` table :
	#

	CREATE TABLE `auth_device` (
	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
	  `auth` BINARY(16) NOT NULL COMMENT 'Identificador unico de Auth',
	  `device_type` tinyint(2) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Tipo del dispositivo',
	  `device_token` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Token del dispositivo',
	  `last_date` timestamp NULL DEFAULT current_timestamp(),
	  PRIMARY KEY (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dispositivos vinculados a auth';
	*/

	private static final Logger LOGGER  = Logger.getLogger(AuthDeviceCreation.class.getName());

	public static final AuthDeviceCreation AUTH_DEVICE_CREATION = new AuthDeviceCreation();

	private AuthDeviceCreation() {
		super();
	}
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info( "Creacion table `auth_device`" );
		String sql = "CREATE TABLE IF NOT EXISTS `auth_device` (\n" + 
				"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',\n" + 
				"`auth` BINARY(16) NOT NULL COMMENT 'Identificador unico de Auth',\n" + 
				"`device_type` tinyint(2) COLLATE latin1_spanish_ci NOT NULL COMMENT 'Tipo del dispositivo',\n" + 
				"`device_token` varchar(128) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Token del dispositivo',\n" + 
				"`last_date` timestamp NULL DEFAULT current_timestamp() COMMENT 'Ultima conexion',\n" + 
				"PRIMARY KEY (`id`)\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Dispositivos vinculados a auth';";
		
		try {
			dslContext.execute(sql);
			LOGGER.info("[table 'auth_device' CREATED!]");
		} catch (Exception e) {
			LOGGER.warning("[table 'auth_device' NOT CREATED!]");
			e.printStackTrace();
		}
		LOGGER.info("[END]");
	}
	
}
