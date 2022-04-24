package net.aonsolutions.db.up2date.task;

import java.sql.Connection;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Domain;
import com.esferalia.aon.jooq.tables.TaskComment;
import com.esferalia.aon.jooq.tables.TaskHolder;
import com.esferalia.aon.jooq.tables.TaskWorkflow;
import com.esferalia.aon.jooq.tables.User;

import net.aonsolutions.db.up2date.Update;

public class TaskComment2TaskWorkfow implements Update{
	public static final TaskComment2TaskWorkfow TASK_COMMENT_2_TASK_WORKFLOW = new TaskComment2TaskWorkfow();

	private TaskComment2TaskWorkfow() {
		super();
	}
	
	@Override
	public void upgrade(Connection conn) {
		Settings settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);

		DSLContext dslContext = DSL.using(conn, SQLDialect.MARIADB, settings);

		System.out.println("[START]");
		
		dslContext.select().from(TaskComment.TASK_COMMENT)
		.where(TaskComment.TASK_COMMENT.SOURCE.isNull())
		.fetchInto(TaskComment.TASK_COMMENT).stream().forEach(r ->{
			dslContext.insertInto(TaskWorkflow.TASK_WORKFLOW)
			.set(TaskWorkflow.TASK_WORKFLOW.DOMAIN, r.getDomain())
			.set(TaskWorkflow.TASK_WORKFLOW.TASK, r.getTask())
			.set(TaskWorkflow.TASK_WORKFLOW.TASK_HOLDER, getTaskHolder(dslContext, r.getDomain(), r.getCreationUser()))
			.set(TaskWorkflow.TASK_WORKFLOW.TYPE, (byte) 7)
			.set(TaskWorkflow.TASK_WORKFLOW.COMMENT, r.getComment())
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_USER, r.getCreationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.CREATION_DATE, r.getCreationDate())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_USER, r.getModificationUser())
			.set(TaskWorkflow.TASK_WORKFLOW.MODIFICATION_DATE, r.getModificationDate())
			.execute();
			
			dslContext.update(TaskComment.TASK_COMMENT)
			.set(TaskComment.TASK_COMMENT.SOURCE,  0)
			.where(TaskComment.TASK_COMMENT.ID.eq(r.getId()))
			.execute();
		});
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
