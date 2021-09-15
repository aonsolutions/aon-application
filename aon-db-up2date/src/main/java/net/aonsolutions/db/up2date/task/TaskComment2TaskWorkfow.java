package net.aonsolutions.db.up2date.task;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.TaskComment;
import com.esferalia.aon.jooq.tables.TaskWorkflow;

import net.aonsolutions.db.up2date.Update;

public class TaskComment2TaskWorkfow implements Update{
	
	private static final Logger LOGGER  = Logger.getLogger(TaskComment2TaskWorkfow.class.getName());
	public static final TaskComment2TaskWorkfow TASK_EVEMT_2_TASK_WORKFLOW = new TaskComment2TaskWorkfow();

	private TaskComment2TaskWorkfow() {
		super();
	}
	
	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		
		dslContext.select().from(TaskComment.TASK_COMMENT).fetchInto(TaskComment.TASK_COMMENT).stream().forEach(r -> 
			dslContext.insertInto(TaskWorkflow.TASK_WORKFLOW)
			.set(TaskWorkflow.TASK_WORKFLOW.DOMAIN, r.getDomain())
			.set(TaskWorkflow.TASK_WORKFLOW.TASK, r.getTask())
//			.set(TaskWorkflow.TASK_WORKFLOW.TASK_HOLDER, )
			.set(TaskWorkflow.TASK_WORKFLOW.TYPE, (byte) 2)
			.set(TaskWorkflow.TASK_WORKFLOW.COMMENT, r.getComment())
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_USER, r.getCreationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_DATE, r.getCreationDate())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_USER, r.getModificationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_DATE, r.getModificationDate())
			.execute());
		LOGGER.info("[END]");

	}
}
