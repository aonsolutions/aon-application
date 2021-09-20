package net.aonsolutions.db.up2date.project;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class ProjectHolderCreation implements Update {

	/**
		#
		# Structure for the `project_holder` table : 
		#

		CREATE TABLE `project_holder` (
	  		`id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',
	  		`domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',
	  		`project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',
	  		`start_date` datetime DEFAULT NULL COMMENT 'Fecha Inicio',
	  		`end_date` datetime DEFAULT NULL COMMENT 'Fecha Fin',
	  		`workgroup` int(4) DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo',
	  		`task_holder` int(4) DEFAULT NULL COMMENT 'Identificador del Operario',
	  		PRIMARY KEY (`id`),
	  		KEY `IDX_PROJECT_HOLDER_DOMAIN` (`domain`),
	  		KEY `IDX_PROJECT_HOLDER_PROJECT` (`project`),
	  		KEY `IDX_PROJECT_HOLDER_WORKGROUP` (`workgroup`),
	  		KEY `IDX_PROJECT_HOLDER_TASK_HOLDER` (`task_holder`),
	  		CONSTRAINT `FK_PROJECT_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),
	  		CONSTRAINT `FK_PROJECT_HOLDER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),
	  		CONSTRAINT `FK_PROJECT_HOLDER_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`)
	  		CONSTRAINT `FK_PROJECT_HOLDER_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)
		) ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Project y Project Holder';
	*/
	
	private static final Logger LOGGER  = Logger.getLogger(ProjectHolderCreation.class.getName());

	public static final ProjectHolderCreation PROJECT_HOLDER_CREATION = new ProjectHolderCreation();

	private ProjectHolderCreation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		LOGGER.info("Creation table `project_holder`");
	
		String sql = "CREATE TABLE IF NOT EXISTS `project_holder` (" 
				+ " `id` int(4) NOT NULL AUTO_INCREMENT COMMENT 'Identificador unico',"
				+ " `domain` int(4) NOT NULL COMMENT 'Identificador del Dominio',"
				+ " `project` int(4) NOT NULL COMMENT 'Identificador del Proyecto',"
				+ " `start_date` datetime DEFAULT NULL COMMENT 'Fecha Inicio',"
				+ " `end_date` datetime DEFAULT NULL COMMENT 'Fecha Fin',"
				+ " `workgroup` int(4) DEFAULT NULL COMMENT 'Identificador del Grupo de Trabajo',"
				+ " `task_holder` int(4) DEFAULT NULL COMMENT 'Identificador del Operario',"
				+ " PRIMARY KEY (`id`),"
				+ " KEY `IDX_PROJECT_HOLDER_DOMAIN` (`domain`),"
				+ " KEY `IDX_PROJECT_HOLDER_PROJECT` (`project`),"
				+ " KEY `IDX_PROJECT_HOLDER_WORKGROUP` (`workgroup`),"
				+ " KEY `IDX_PROJECT_HOLDER_TASK_HOLDER` (`task_holder`),"
				+ " CONSTRAINT `FK_PROJECT_HOLDER_DOMAIN` FOREIGN KEY (`domain`) REFERENCES `domain` (`id`),"
				+ " CONSTRAINT `FK_PROJECT_HOLDER_PROJECT` FOREIGN KEY (`project`) REFERENCES `project` (`id`),"
				+ " CONSTRAINT `FK_PROJECT_HOLDER_WORKGROUP` FOREIGN KEY (`workgroup`) REFERENCES `workgroup` (`id`),"
				+ " CONSTRAINT `FK_PROJECT_HOLDER_TASK_HOLDER` FOREIGN KEY (`task_holder`) REFERENCES `task_holder` (`registry`)"
				+ ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_spanish_ci COMMENT='Relacion entre Project y Project Holder';";
		

		dslContext.execute(sql);
		
		LOGGER.info("[END]");
	}

}
