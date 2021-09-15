package net.aonsolutions.db.up2date.task;

import java.sql.Connection;
import java.util.logging.Logger;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.TaskEvent;
import com.esferalia.aon.jooq.tables.TaskWorkflow;

import net.aonsolutions.db.up2date.Update;

public class TaskEvent2TaskWorkfow implements Update {

	private static final Logger LOGGER  = Logger.getLogger(TaskEvent2TaskWorkfow.class.getName());

	public static final TaskEvent2TaskWorkfow TASK_COMMENT_2_TASK_WORKFLOW = new TaskEvent2TaskWorkfow();

	private TaskEvent2TaskWorkfow() {
		super();
	}
	
	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);

		LOGGER.info("[START]");
		
		dslContext.select().from(TaskEvent.TASK_EVENT).fetchInto(TaskEvent.TASK_EVENT).stream().forEach(r ->
			dslContext.insertInto(TaskWorkflow.TASK_WORKFLOW)
			.set(TaskWorkflow.TASK_WORKFLOW.DOMAIN, r.getDomain())
			.set(TaskWorkflow.TASK_WORKFLOW.TASK, r.getTask())
//			.set(TaskWorkflow.TASK_WORKFLOW.TASK_HOLDER, )
			.set(TaskWorkflow.TASK_WORKFLOW.TYPE, getTaskWorkflowType(r.getEvent()))
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_USER, r.getCreationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_DATE, r.getCreationDate())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_USER, r.getModificationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_DATE, r.getModificationDate())
			.execute());
		LOGGER.info("[END]");
	}
	
	private Byte getTaskWorkflowType(String event) {
		if("opened".equalsIgnoreCase(event)) {
			return (byte) 0;
		} else if("closed".equalsIgnoreCase(event)) {
			return (byte) 1;
		} else if("reopened".equalsIgnoreCase(event)) {
			return (byte) 2;
		} else if("duplicate".equalsIgnoreCase(event)) {
			return (byte) 3;
		} else if("liberate".equalsIgnoreCase(event)) {
			return (byte) 4;
		} else if("deleted".equalsIgnoreCase(event)) {
			return (byte) 5;
		} else if("restore".equalsIgnoreCase(event)) {
			return (byte) 6;
		} else return null;		
	}

}
