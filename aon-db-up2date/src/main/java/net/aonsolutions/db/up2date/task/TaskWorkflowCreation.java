package net.aonsolutions.db.up2date.task;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TaskWorkflowCreation implements Update {

//	#
//	# Structure for the `task_workflow` table : 
//	#
//
//	CREATE TABLE `task_workflow` (
//	  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
//	  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
//	  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea',
//	  `task_holder` int(4) DEFAULT NULL COMMENT 'Identificador del Operario',
//	  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo del flujo de Tareas',
//	  `comment` text COLLATE latin1_spanish_ci COMMENT 'Comentario de la Tarea',
//	  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion',
//	  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion',
//	  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion',
//	  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion',
//	  PRIMARY KEY (`id`),
//	  KEY `IDX_TASK_WORKFLOW_DOMAIN` (`domain`),
//	  KEY `IDX_TASK_WORKFLOW_TASK` (`task`),
//	  KEY `IDX_TASK_WORKFLOW_TASK_HOLDER` (`task_holder`),
//	  CONSTRAINT `FK_TASK_WORKFLOW_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
//	  CONSTRAINT `FK_TASK_WORKFLOW_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`),
//	  CONSTRAINT `FK_TASK_WORKFLOW_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
//	) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Flujo de Tareas';

	public static final TaskWorkflowCreation TASK_WORKFLOW_CREATION = new TaskWorkflowCreation();

	private TaskWorkflowCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		System.out.println( "Creacion table `task_workflow`" );
		


		String SQL = 
				"CREATE TABLE IF NOT EXISTS `task_workflow` (" + 
				"  `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico'," + 
				"  `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio'," + 
				"  `task` int(4) NOT NULL COMMENT 'Identificador de la tarea'," + 
				"  `task_holder` int(4) DEFAULT NULL COMMENT 'Identificador del Operario'," + 
				"  `type` tinyint(2) DEFAULT NULL COMMENT 'Tipo del flujo de Tareas'," + 
				"  `comment` text COLLATE latin1_spanish_ci COMMENT 'Comentario de la Tarea'," + 
				"  `creation_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de creacion'," + 
				"  `creation_date` datetime DEFAULT NULL COMMENT 'Fecha de creacion'," + 
				"  `modification_user` varchar(16) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Usuario de modificacion'," + 
				"  `modification_date` datetime DEFAULT NULL COMMENT 'Fecha de modificacion'," + 
				"  PRIMARY KEY (`id`)," + 
				"  KEY `IDX_TASK_WORKFLOW_DOMAIN` (`domain`)," + 
				"  KEY `IDX_TASK_WORKFLOW_TASK` (`task`)," + 
				"  KEY `IDX_TASK_WORKFLOW_TASK_HOLDER` (`task_holder`)," + 
				"  CONSTRAINT `FK_TASK_WORKFLOW_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`)," + 
				"  CONSTRAINT `FK_TASK_WORKFLOW_TASK` FOREIGN KEY (`task`) REFERENCES `task` (`id`)," + 
				"  CONSTRAINT `FK_TASK_WORKFLOW_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)" + 
				") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Tareas y Flujo de Tareas';";
		;

		dslContext.execute(SQL);
		
		System.out.println("[END]");
	}

}
