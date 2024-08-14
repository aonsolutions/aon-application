package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskWorkflow.TASK_WORKFLOW;
import static com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TASK_HOLDER_ALIAS;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskWorkflowFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskWorkflowProperties;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskWorkflowDAO {
	
	private TaskWorkflowDAO() {
		
	}

	private static final TaskWorkflowPropertiesDAO TASK_WORKFLOW_PROPERTIES = new TaskWorkflowPropertiesDAO();
	protected static class TaskWorkflowPropertiesDAO implements TaskWorkflowProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, TaskWorkflowFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(TaskWorkflowFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.DOMAIN);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.TASK);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.TASK_HOLDER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.TYPE);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.COMMENT);}		
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.MODIFICATION_DATE);}
		@Override public Property<String> getNotificationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.NOTIFICATION_USER);}
		@Override public Property<Timestamp> getNotificationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.NOTIFICATION_DATE);}
		@Override public Property<String> getEmailProperty() {return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.EMAIL);}
	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, TaskWorkflowFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TASK_WORKFLOW)
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TASK_WORKFLOW.TASK_HOLDER))
				.leftOuterJoin(TASK_HOLDER_ALIAS).on(TASK_HOLDER_ALIAS.ID.eq(TASK_HOLDER.REGISTRY))
				.where(TASK_WORKFLOW_PROPERTIES.getConditions(filter));
	}
	
	public static Stream<TaskWorkflow> getStream(AONContext ctx, TaskWorkflowFilter filter){	
		return select(ctx, filter).fetch().stream().map(new TaskWorkflowFiller());
	}
	
	public static Stream<TaskWorkflow> getStream(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new TaskWorkflowFiller());
	}
	
	public static List<TaskWorkflow> getList(AONContext ctx, TaskWorkflowFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<TaskWorkflow> getList(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static TaskWorkflow get(AONContext ctx, TaskWorkflowFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new TaskWorkflowFiller())
			.findFirst().orElse(new TaskWorkflow());
	}
	
	public static TaskWorkflow save(AONContext ctx, TaskWorkflow taskWorkflow) {
		// TODO AUTOCOMPLETE && VALIDATE.
		return taskWorkflow.getId() != null && taskWorkflow.getId()>0
			? update(ctx, taskWorkflow)
			: insert(ctx, taskWorkflow);
	}
	
	public static void updateTaskWorkflowBetween(AONContext ctx, TaskWorkflowFilter filter) {
		ctx.getDslContext().update(TASK_WORKFLOW)
			.set(TASK_WORKFLOW.NOTIFICATION_DATE,AonDateUtils.toTimestamp(new Date()))
			.set(TASK_WORKFLOW.NOTIFICATION_USER, ctx.getUser())
			.where(TASK_WORKFLOW_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	
	public static TaskWorkflow update(AONContext ctx, TaskWorkflow taskWorkflow) {
		taskWorkflow.setModificationDate(AonDateUtils.toTimestamp(new Date()));
		ctx.getDslContext().update(TASK_WORKFLOW)
			.set(TASK_WORKFLOW.DOMAIN, taskWorkflow.getDomain())
			.set(TASK_WORKFLOW.TASK, taskWorkflow.getTask())
			.set(TASK_WORKFLOW.TASK_HOLDER, taskWorkflow.getTaskHolder().getId())
			.set(TASK_WORKFLOW.TYPE, taskWorkflow.getType().value())	
			.set(TASK_WORKFLOW.COMMENT, taskWorkflow.getComment())
			.set(TASK_WORKFLOW.MODIFICATION_DATE, AonDateUtils.toTimestamp(taskWorkflow.getModificationDate()))
			.set(TASK_WORKFLOW.MODIFICATION_USER, ctx.getUser())
			.where(TASK_WORKFLOW.ID.eq(taskWorkflow.getId()))
			.execute();
		ctx.log().info("UPDATE TASK_WORKFLOW id: " + taskWorkflow.getId());		
		return taskWorkflow;
	}
	
	public static TaskWorkflow insert(AONContext ctx, TaskWorkflow workflow) {
		Integer id = ctx.getDslContext().insertInto(TASK_WORKFLOW)
				.set(TASK_WORKFLOW.DOMAIN, workflow.getDomain())
				.set(TASK_WORKFLOW.TASK, workflow.getTask())
				.set(TASK_WORKFLOW.TASK_HOLDER, workflow.getTaskHolder().getId())
				.set(TASK_WORKFLOW.TYPE, workflow.getType().value())	
				.set(TASK_WORKFLOW.COMMENT, workflow.getComment())
				.set(TASK_WORKFLOW.EMAIL, workflow.getEmail())
				.set(TASK_WORKFLOW.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(TASK_WORKFLOW.CREATION_USER, ctx.getUser())
				.set(TASK_WORKFLOW.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
				.set(TASK_WORKFLOW.MODIFICATION_USER, ctx.getUser())
			.returning(TASK_WORKFLOW.ID).fetchOne().getId();
		ctx.log().debug("INSERT TASK_WORKFLOW id: " + id);			
		return workflow.setId(id);
	}	
	
	public static void delete(AONContext ctx, Integer id){
		ctx.getDslContext().delete(TASK_WORKFLOW)
		.where(TASK_WORKFLOW.ID.eq(id))
		.execute();
		ctx.log().debug("DELETE TASK_WORKFLOW id:" + id);
	}
	
	public static void deleteByTask(AONContext ctx, Integer id){
		ctx.getDslContext().delete(TASK_WORKFLOW)
		.where(TASK_WORKFLOW.TASK.eq(id))
		.execute();
		ctx.log().debug("DELETE TASK_WORKFLOW task:" + id);
	}
	
	public static class TaskWorkflowFiller extends Filler implements Function<Record, TaskWorkflow> {

		@Override
		public TaskWorkflow apply(Record r) {
			return new TaskWorkflow()
				.setId(getValue(r, TASK_WORKFLOW.ID))
				.setDomain(getValue(r, TASK_WORKFLOW.DOMAIN))
				.setTask(getValue(r, TASK_WORKFLOW.TASK))
				.setTaskHolder(checkField(r, TASK_HOLDER.REGISTRY)
					? TaskHolderFiller.build(r) : new TaskHolder().setRegistry(getValue(r, TASK_WORKFLOW.TASK_HOLDER)))
				.setType(TaskWorkflowType.safeValueOf(getValue(r, TASK_WORKFLOW.TYPE)))
				.setComment(getValue(r, TASK_WORKFLOW.COMMENT))
				.setCreationDate(getValue(r, TASK_WORKFLOW.CREATION_DATE))
				.setCreationUser(getValue(r, TASK_WORKFLOW.CREATION_USER))
				.setEmail(getValue(r, TASK_WORKFLOW.EMAIL))
				.setModificationDate(getValue(r, TASK_WORKFLOW.MODIFICATION_DATE))
				.setModificationUser(getValue(r, TASK_WORKFLOW.MODIFICATION_USER))
				.setNotificationDate(getValue(r, TASK_WORKFLOW.NOTIFICATION_DATE))
				.setNotificationUser(getValue(r, TASK_WORKFLOW.NOTIFICATION_USER));
		}
	}
}
