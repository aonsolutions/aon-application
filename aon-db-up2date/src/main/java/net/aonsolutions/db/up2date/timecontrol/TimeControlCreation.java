package net.aonsolutions.db.up2date.timecontrol;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TimeControlCreation implements Update {

//	#
//	# Structure for the `timecontrol` table :
//	#
//
//	CREATE TABLE `timecontrol` (
//		`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',
//		`domain` int(4) NOT NULL COMMENT 'Dominio',
//		`task_holder` int(4) NOT NULL COMMENT 'Identificador del operario',
//		`status` tinyint(2) NOT NULL COMMENT 'Estado del control de horario',
//		`date` datetime NOT NULL COMMENT 'Fecha del control de horario',
//		`comments` text COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios del control de horario',
//		`location` int(4) DEFAULT NULL COMMENT 'Ubicación del Operario',
//		`coordinates` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Coordenadas de la Ubicación',
//		PRIMARY KEY (`id`),
//		KEY `IDX_TIMECONTROL_DOMAIN` (`domain`),
//		KEY `IDX_TIMECONTROL_TASK_HOLDER` (`task_holder`),
//	 	KEY `IDX_TIMECONTROL_LOCATION` (`location`),
//		CONSTRAINT `FK_TIMECONTROL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//		CONSTRAINT `FK_TIMECONTROL_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`),
//		CONSTRAINT `FK_TIMECONTROL_LOCATION` FOREIGN KEY (`location`) REFERENCES `location` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Control de Horario';


	public static TimeControlCreation TIMECONTROL_CREATION = new TimeControlCreation();

	private TimeControlCreation() {
		super();
	}
	
	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `timecontrol`" );

		String SQL = "CREATE TABLE IF NOT EXISTS `timecontrol` (`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'ID unico del vinculo',`domain` int(4) NOT NULL COMMENT 'Dominio',`task_holder` int(4) NOT NULL COMMENT 'Identificador del operario', `status` tinyint(2) NOT NULL COMMENT 'Estado del control de horario',`date` datetime NOT NULL COMMENT 'Fecha del control de horario',`comments` text COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Comentarios del control de horario', `location` int(4) DEFAULT NULL COMMENT 'Ubicación del Operario', `coordinates` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Coordenadas de la Ubicación', PRIMARY KEY (`id`), KEY `IDX_TIMECONTROL_DOMAIN` (`domain`),KEY `IDX_TIMECONTROL_TASK_HOLDER` (`task_holder`), KEY `IDX_TIMECONTROL_LOCATION` (`location`), CONSTRAINT `FK_TIMECONTROL_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),CONSTRAINT `FK_TIMECONTROL_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`), CONSTRAINT `FK_TIMECONTROL_LOCATION` FOREIGN KEY (`location`) REFERENCES `location` (`id`)) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Control de Horario';";

		try {
			dslContext.execute(SQL);
			System.out.println("[table 'timecontrol' CREATED!]");
		} catch (Throwable t) {
			System.out.println("[table 'timecontrol' NOT CREATED!]");
		}
		System.out.println("[END]");
	}

}
