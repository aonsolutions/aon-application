package net.aonsolutions.db.up2date.notification;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class NotificationCreation implements Update {

	/**
	#
	# Structure for the `notification` table :
	#

	CREATE TABLE `notification` (
	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
	  `domain` int(4) NOT NULL DEFAULT 0 COMMENT 'Dominio',
	  `date` datetime DEFAULT NULL COMMENT 'Fecha de la notificacion',
	  `title` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Titulo de la notificacion',
	  `body` text COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Mensaje de la notificacion',
	  `source` tinyint(2) DEFAULT NULL COMMENT 'Tipo de proceso',
	  `source_id` int(4) DEFAULT NULL COMMENT 'ID del proceso',
	  `sender` binary(16) DEFAULT NULL COMMENT 'ID de AUTH',
	  `priority` tinyint(2) DEFAULT 0 COMMENT 'Prioridad de la notificacion',
	  PRIMARY KEY (`id`),
	  KEY `IDX_NOTIFICATION_DOMAIN` (`domain`),
	  CONSTRAINT `FK_NOTIFICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notification';
	*/
	
	private static final Logger LOGGER  = Logger.getLogger(NotificationCreation.class.getName());

	public static final NotificationCreation NOTIFICATION_CREATION = new NotificationCreation();

	private NotificationCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info( "Creacion table `notification`" );

		String sql = "CREATE TABLE IF NOT EXISTS  `notification`(" + 
				"`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo'," + 
				"`domain` int(4) NOT NULL COMMENT 'Dominio'," + 
				"`date` datetime DEFAULT NULL COMMENT 'Fecha de la notificacion'," + 
				"`title`  varchar(64) COLLATE latin1_spanish_ci  COMMENT 'Titulo de la notificacion'," + 
				"`body` TEXT COMMENT 'Mensaje de la notificacion'," + 
				"`source` tinyint(2) DEFAULT NULL COMMENT 'Tipo de proceso'," + 
				"`source_id` int(4) DEFAULT NULL COMMENT 'ID del proceso'," + 
				"`sender` BINARY(16) DEFAULT NULL COMMENT 'ID de AUTH'," + 
				"`priority` tinyint(2) DEFAULT '0' COMMENT 'Prioridad de la notificacion'," + 
				"PRIMARY KEY (`id`)," + 
				"KEY `IDX_NOTIFICATION_DOMAIN` (`domain`)," + 
				"CONSTRAINT `FK_NOTIFICATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)" + 
				") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Notification';";
		try {
			dslContext.execute(sql);
			LOGGER.info("[table 'notification' CREATED!]");
		} catch (Exception e) {
			LOGGER.warning("[table 'notification' NOT CREATED!]");
			e.printStackTrace();
		}
		LOGGER.info("[END]");
	}

}
