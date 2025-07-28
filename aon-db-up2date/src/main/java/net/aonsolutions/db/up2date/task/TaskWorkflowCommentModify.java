package net.aonsolutions.db.up2date.task;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class TaskWorkflowCommentModify implements Update {
	
	
	public static final TaskWorkflowCommentModify TASK_WORKFLOW_COMMENT_MODIFY = new TaskWorkflowCommentModify();

	private TaskWorkflowCommentModify() {
		super();
	}

	@Override
	public void upgrade(Connection connection) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
		
		dslContext.execute("ALTER TABLE  `task_workflow` MODIFY `comment` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Comentario de la Tarea';");

	}

}
