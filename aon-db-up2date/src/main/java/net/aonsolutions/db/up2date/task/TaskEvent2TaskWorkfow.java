package net.aonsolutions.db.up2date.task;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.Date;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.TaskEvent;
import com.esferalia.aon.jooq.tables.TaskHolder;
import com.esferalia.aon.jooq.tables.TaskWorkflow;
import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;

public class TaskEvent2TaskWorkfow implements Update {

	public static final TaskEvent2TaskWorkfow TASK_EVENT_2_TASK_WORKFLOW = new TaskEvent2TaskWorkfow();

	private TaskEvent2TaskWorkfow() {
		super();
	}
	
	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		
		dslContext.select().from(TaskEvent.TASK_EVENT)
		.where(TaskEvent.TASK_EVENT.MODIFICATION_DATE.isNull())
		.fetchInto(TaskEvent.TASK_EVENT).stream().forEach(r -> {
			dslContext.insertInto(TaskWorkflow.TASK_WORKFLOW)
			.set(TaskWorkflow.TASK_WORKFLOW.DOMAIN, r.getDomain())
			.set(TaskWorkflow.TASK_WORKFLOW.TASK, r.getTask())
			.set(TaskWorkflow.TASK_WORKFLOW.TASK_HOLDER, getTaskHolder(dslContext, r.getDomain(), r.getCreationUser()))
			.set(TaskWorkflow.TASK_WORKFLOW.TYPE, getTaskWorkflowType(r.getEvent()))
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_USER, r.getCreationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_DATE, r.getCreationDate())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_USER, r.getModificationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_DATE, r.getModificationDate())
			.execute();
			
			dslContext.update(TaskEvent.TASK_EVENT)
			.set(TaskEvent.TASK_EVENT.MODIFICATION_DATE,  new Timestamp(new Date().getTime()))
			.where(TaskEvent.TASK_EVENT.ID.eq(r.getId()))
			.execute();
		});
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
	
	
	private Integer getTaskHolder(DSLContext dslContext, Integer domain, String login) {
		Integer id = dslContext.select()
				.from(User.USER)
				.where(User.USER.DOMAIN.eq(domain))
				.and(User.USER.LOGIN.eq(login))
				.fetch().stream().map(r -> r.getValue(User.USER.ID))
				.findFirst().orElse(null);
		if(id == null) {
			Integer parentDomain = dslContext.select()
				.from(Domain.DOMAIN)
				.where(Domain.DOMAIN.ID.eq(domain))
				.fetch().stream().map(r -> r.getValue(Domain.DOMAIN.PARENT))
				.findFirst().orElse(null);
			
			id = dslContext.select()
					.from(User.USER)
					.where(User.USER.DOMAIN.eq(parentDomain))
					.and(User.USER.LOGIN.eq(login))
					.fetch().stream().map(r -> r.getValue(User.USER.ID))
					.findFirst().orElse(null);
		}
		return dslContext.select()
				.from(TaskHolder.TASK_HOLDER)
				.where(TaskHolder.TASK_HOLDER.USER_ID.eq(id))
				.fetch().stream().map(r -> r.getValue(TaskHolder.TASK_HOLDER.REGISTRY))
				.findFirst().orElse(null);
	}

}
