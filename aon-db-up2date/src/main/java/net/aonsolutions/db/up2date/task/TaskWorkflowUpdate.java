package net.aonsolutions.db.up2date.task;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import net.aonsolutions.db.up2date.Update;

public class TaskWorkflowUpdate implements Update {
	
	private static final Logger LOGGER  = Logger.getLogger(TaskWorkflowUpdate.class.getName());
	private static final String TASK_WORKFLOW = "task_workflow";
	public static final TaskWorkflowUpdate TASK_WORKFLOW_UPDATE = new TaskWorkflowUpdate();

	private TaskWorkflowUpdate() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
		
		LOGGER.info("[START]");
		LOGGER.info("Update table `task_workflow`");
		
		try {
			String sql = "ALTER TABLE `task_workflow` ADD `email` varchar(64) COLLATE latin1_spanish_ci DEFAULT NULL COMMENT 'Email del emisor' AFTER `task_holder`";
			dslContext.execute(sql);
	
			dslContext.alterTable(DSL.name(TASK_WORKFLOW))
				.addColumn("notification_user", SQLDataType.VARCHAR(16).nullable(true))
				.execute();
			
			dslContext.alterTable(DSL.name(TASK_WORKFLOW))
				.addColumn("notification_date", SQLDataType.TIMESTAMP.defaultValue(DSL.currentTimestamp()))
				.execute();
			
			LOGGER.info("[table `task_workflow` Update!]");
		} catch (Exception e) {
			LOGGER.warning("[table 'task_workflow' NOT Update!] " + e.getMessage());
			e.printStackTrace();
		}

		LOGGER.info("[END]");
	}

}
