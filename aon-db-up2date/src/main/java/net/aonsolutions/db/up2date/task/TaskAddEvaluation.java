package net.aonsolutions.db.up2date.task;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TaskAddEvaluation implements Update {
	
	private static final Logger LOGGER  = Logger.getLogger(TaskAddEvaluation.class.getName());
	public static final TaskAddEvaluation TASK_ADD_EVALUATION = new TaskAddEvaluation();

	private TaskAddEvaluation() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info("Update table `task`");
		
		try {
//			Field<Byte> evaluation = DSL.field("evaluation", TINYINT.length(2).nullable(true), "Calificacion");
//	
//			dslContext.alterTable(Task.TASK)
//				.addColumn(evaluation)
//				.after("parent")
//				.execute();
//			
			String sql = "ALTER TABLE `task` ADD COLUMN `evaluation` tinyint(2) DEFAULT NULL COMMENT 'Calificacion' AFTER `parent`;";
			dslContext.execute(sql);

			LOGGER.info("[table `task` Update!]");
		} catch (Exception e) {
			LOGGER.warning("[table 'task' NOT Update!] " + e.getMessage());
			e.printStackTrace();
		}

		LOGGER.info("[END]");
	}

}
