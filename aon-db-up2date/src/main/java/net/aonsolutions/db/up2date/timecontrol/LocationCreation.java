package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class LocationCreation implements Update {

	/**
	#
	# Structure for the `location` table :
	#

	CREATE TABLE `location` (
		`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
		`domain` int(4) NOT NULL COMMENT 'Dominio',
		`description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción de la Ubicación',
		`coordinates` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Coordenadas de la Ubicación',
	    `radio` int(4) DEFAULT 50 COMMENT 'Radio de la Ubicación',
		PRIMARY KEY (`id`),
		KEY `IDX_LOCATION_DOMAIN` (`domain`),
		CONSTRAINT `FK_LOCATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ubicación';
	*/
	
	private static final Logger LOGGER  = Logger.getLogger(LocationCreation.class.getName());

	public static final LocationCreation LOCATION_CREATION = new LocationCreation();

	private LocationCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info( "Creacion table `location`" );

		String sql = "CREATE TABLE IF NOT EXISTS `location` (`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',`domain` int(4) NOT NULL COMMENT 'Dominio', `description` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Descripción de la Ubicación',`coordinates` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Coordenadas de la Ubicación',`radio` int(4) DEFAULT 50 COMMENT 'Radio de la Ubicación', PRIMARY KEY (`id`),KEY `IDX_LOCATION_DOMAIN` (`domain`),CONSTRAINT `FK_LOCATION_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Ubicación';";

		try {
			dslContext.execute(sql);
			LOGGER.info("[table 'location' CREATED!]");
		} catch (Exception e) {
			LOGGER.info("[table 'location' NOT CREATED!]");
			e.printStackTrace();
		}
		LOGGER.info("[END]");
	}

}
