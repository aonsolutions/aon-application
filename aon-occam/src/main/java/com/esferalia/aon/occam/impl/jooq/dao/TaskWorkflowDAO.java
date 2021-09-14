package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.TaskWorkflow.TASK_WORKFLOW;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskWorkflowDAO {

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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_WORKFLOW.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_WORKFLOW.DOMAIN);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_WORKFLOW.TASK);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_WORKFLOW.TASK_HOLDER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK_WORKFLOW.TYPE);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<String>(TASK_WORKFLOW.COMMENT);}		
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK_WORKFLOW.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK_WORKFLOW.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK_WORKFLOW.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK_WORKFLOW.MODIFICATION_DATE);}

	}
	
	public static SelectConditionStep<Record> select(AONContext ctx, TaskWorkflowFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TASK_WORKFLOW)
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TASK_WORKFLOW.TASK_HOLDER))
				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
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
	
	public static LinkedList<TaskWorkflow> getList(AONContext ctx, TaskWorkflowFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<TaskWorkflow> getList(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static TaskWorkflow get(AONContext ctx, TaskWorkflowFilter filter) {
		return select(ctx, filter).limit(1)
			.fetch().stream().map(new TaskWorkflowFiller())
			.findFirst().orElse(new TaskWorkflow());
	}
	
	public static TaskWorkflow save(AONContext ctx, TaskWorkflow taskWorkflow) {
		// TODO AUTOCOMPLETE && VALIDATE.
		return taskWorkflow.getId() != null 
			? update(ctx, taskWorkflow)
			: insert(ctx, taskWorkflow);
	}
	
	public static void updateTaskWorkflowBetween(AONContext ctx, TaskWorkflowFilter filter) {
		ctx.getDslContext().update(TASK_WORKFLOW)
			.set(TASK_WORKFLOW.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
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
		return taskWorkflow;
	}
	
	public static TaskWorkflow insert(AONContext ctx, TaskWorkflow taskWorkflow) {
		Integer id = ctx.getDslContext().insertInto(TASK_WORKFLOW)
				.set(TASK_WORKFLOW.DOMAIN, taskWorkflow.getDomain())
				.set(TASK_WORKFLOW.TASK, taskWorkflow.getTask())
				.set(TASK_WORKFLOW.TASK_HOLDER, taskWorkflow.getTaskHolder().getId())
				.set(TASK_WORKFLOW.TYPE, taskWorkflow.getType().value())	
				.set(TASK_WORKFLOW.COMMENT, taskWorkflow.getComment())
				.set(TASK_WORKFLOW.CREATION_DATE, AonDateUtils.toTimestamp(new Date()))
//				.set(TASK_WORKFLOW.MODIFICATION_DATE, AonDateUtils.toTimestamp(taskWorkflow.getModificationDate()))
				.set(TASK_WORKFLOW.CREATION_USER, ctx.getUser())
				.set(TASK_WORKFLOW.MODIFICATION_USER, ctx.getUser())
			.returning(TASK_WORKFLOW.ID).fetchOne().getId();
		return taskWorkflow.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, TaskWorkflowFilter filter){
		ctx.getDslContext().delete(TASK_WORKFLOW)
		.where(TASK_WORKFLOW_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static class TaskWorkflowFiller implements Function<Record, TaskWorkflow> {

		@Override
		public TaskWorkflow apply(Record r) {
			return new TaskWorkflow()
				.setId(r.getValue(TASK_WORKFLOW.ID))
				.setDomain(r.getValue(TASK_WORKFLOW.DOMAIN))
				.setTask(r.getValue(TASK_WORKFLOW.TASK))
				.setTaskHolder(TaskHolderFiller.build(r, REGISTRY))
				.setType(TaskWorkflowType.safeValueOf(r.getValue(TASK_WORKFLOW.TYPE)))
				.setComment(r.getValue(TASK_WORKFLOW.COMMENT))
				.setCreationDate(r.getValue(TASK_WORKFLOW.CREATION_DATE))
				.setCreationUser(r.getValue(TASK_WORKFLOW.CREATION_USER))
				.setModificationDate(r.getValue(TASK_WORKFLOW.MODIFICATION_DATE))
				.setModificationUser(r.getValue(TASK_WORKFLOW.MODIFICATION_USER));
		}
	}
}
