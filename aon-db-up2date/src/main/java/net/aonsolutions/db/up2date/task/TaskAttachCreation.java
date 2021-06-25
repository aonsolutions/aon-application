package net.aonsolutions.db.up2date.task;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TaskAttachCreation implements Update {

//	#
//	# Structure for the `task_attach` table : 
//	#
//
//	CREATE TABLE `task_attach` (
//	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
//	  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
//	  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
//	  `task_workflow` int(4) DEFAULT NULL COMMENT 'Identificador del Flujo de Tareas',
//	  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto',
//	  `data` mediumblob COMMENT 'Archivo Adjunto en binario',
//	  PRIMARY KEY (`id`),
//	  KEY `IDX_TASK_ATTACH_DOMAIN` (`domain`),
//	  KEY `IDX_TASK_ATTACH_TASK` (`task`),
//	  KEY `IDX_TASK_ATTACH_TASK_WORKFLOW` (`task_workflow`),
//	  CONSTRAINT `FK_TASK_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//	  CONSTRAINT `FK_TASK_ATTACH_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
//	  CONSTRAINT `FK_TASK_ATTACH_TASK_WORKFLOW` FOREIGN KEY (`task_workflow`) REFERENCES `task_workflow` (`id`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas, Flujo de Tareas y Archivos adjuntos';


	public static final TaskAttachCreation TASK_ATTACH_CREATION = new TaskAttachCreation();

	private TaskAttachCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `task_attach`" );

		String SQL =
				"CREATE TABLE IF NOT EXISTS `task_attach` (" + 
				"  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico'," + 
				"  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio'," + 
				"  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea'," + 
				"  `task_workflow` int(4) DEFAULT NULL COMMENT 'Identificador del Flujo de Tareas'," + 
				"  `mimeType` tinyint(2) DEFAULT '0' COMMENT 'Mime Type del Archivo Adjunto'," + 
				"  `data` mediumblob COMMENT 'Archivo Adjunto en binario'," + 
				"  PRIMARY KEY (`id`)," + 
				"  KEY `IDX_TASK_ATTACH_DOMAIN` (`domain`)," + 
				"  KEY `IDX_TASK_ATTACH_TASK` (`task`)," + 
				"  KEY `IDX_TASK_ATTACH_TASK_WORKFLOW` (`task_workflow`)," + 
				"  CONSTRAINT `FK_TASK_ATTACH_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)," + 
				"  CONSTRAINT `FK_TASK_ATTACH_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)," + 
				"  CONSTRAINT `FK_TASK_ATTACH_TASK_WORKFLOW` FOREIGN KEY (`task_workflow`) REFERENCES `task_workflow` (`id`)" + 
				") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas, Flujo de Tareas y Archivos adjuntos';";


		dslContext.execute(SQL);
		
		System.out.println("[END]");
	}

}
