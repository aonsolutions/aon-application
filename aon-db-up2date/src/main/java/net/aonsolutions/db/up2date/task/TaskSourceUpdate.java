package net.aonsolutions.db.up2date.task;

import static com.esferalia.aon.jooq.tables.Task.TASK;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TaskSourceUpdate implements Update {
	
	private static final Logger LOGGER  = Logger.getLogger(TaskSourceUpdate.class.getName());
	public static final TaskSourceUpdate TASK_SOURCE_UPDATE = new TaskSourceUpdate();

	private TaskSourceUpdate() {
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
			
			dslContext
			.update(TASK)
			.set(TASK.SOURCE, (byte)8)
			.where(TASK.SOURCE.eq((byte)3))
			.and(TASK.PARENT.isNotNull())
			.execute();
			
			LOGGER.info("[table `task` Update!]");
		} catch (Exception e) {
			LOGGER.warning("[table 'task' NOT Update!] " + e.getMessage());
			e.printStackTrace();
		}

		LOGGER.info("[END]");
	}

}
