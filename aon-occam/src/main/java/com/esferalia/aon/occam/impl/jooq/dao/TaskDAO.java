package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Task.TASK;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.project.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskPeriod;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskDAO {
	
	private static final TaskPropertiesDAO TASK_PROPERTIES = new TaskPropertiesDAO();
	protected static class TaskPropertiesDAO implements TaskProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, TaskFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(TaskFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.DOMAIN);}
		@Override public Property<Integer> getActivityTypeProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.ACTIVITY_TYPE);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(TASK.COMMENTS);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(TASK.DESCRIPTION);}
		@Override public Property<Timestamp> getDueDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK.DUE_DATE);}
		@Override public Property<Timestamp> getEndDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK.END_DATE);}
		@Override public Property<String> getGtaskIdProperty() {return new FilterDAO.PropertyDAO<String>(TASK.GTASK_ID);}
		@Override public Property<String> getGtasklisIdProperty() {return new FilterDAO.PropertyDAO<String>(TASK.GTASKLIST_ID);}
		@Override public Property<Byte> getPercentProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.PERCENT);}
		@Override public Property<Byte> getPriorityProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.PRIORITY);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.PROJECT);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.REGISTRY);}
		@Override public Property<Byte> getRepeatPeriodProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.REPEAT_PERIOD);}
		@Override public Property<Integer> getSenderProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.SENDER);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.SOURCE_ID);}
		@Override public Property<Timestamp> getStartDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK.START_DATE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.STATUS);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.TASK_HOLDER);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.WORKGROUP);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.NUMBER);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK.MODIFICATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK.CREATION_DATE);}
		@Override public Property<Integer> getParentProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.PARENT);}
	}
	
	public static SelectSeekStep1<Record, Timestamp> select(AONContext ctx, TaskFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TASK)
				.where(TASK_PROPERTIES.getConditions(filter))
				.orderBy(TASK.CREATION_DATE.desc());
	}
	
	public static Stream<Task> getStream(AONContext ctx, TaskFilter filter){	
		return select(ctx, filter).fetchInto(TASK).stream().map(new TaskFiller());
	}
	
	public static Stream<Task> getStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetchInto(TASK).stream().map(new TaskFiller());
	}
	
	public static LinkedList<Task> getList(AONContext ctx, TaskFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Task> getList(AONContext ctx, TaskFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Task get(AONContext ctx, TaskFilter filter) {
		Task task = select(ctx, filter).limit(1)
			.fetchInto(TASK).stream().map(new TaskFiller())
			.findFirst().orElse(new Task());
		if(task.getId() != null) {
			task.setWorkflows(TaskWorkflowDAO.getList(ctx, f -> f.getTaskProperty().eq(task.getId())));
		}
		return task;
	}
	
	public static Task save(AONContext ctx, Task task) {
		// TODO AUTOCOMPLETE && VALIDATE.
		return task.getId() != null 
			? update(ctx, task)
			: insert(ctx, task);
	}
	
	public static Task update(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)
			.set(TASK.DESCRIPTION, task.getTitle())
//			.set(TASK.START_DATE, AonDateUtils.toTimestamp(task.getStartDate()))
			.set(TASK.END_DATE, AonDateUtils.toTimestamp(task.getEndDate()))
			.set(TASK.DUE_DATE, AonDateUtils.toTimestamp(task.getDueDate()))
			.set(TASK.PRIORITY, task.getPriority().value())
			.set(TASK.STATUS, task.getStatus().value())
			.set(TASK.PERCENT, task.getPercent())
			.set(TASK.TASK_HOLDER, task.getTaskHolder().getId())
			.set(TASK.WORKGROUP, task.getWorkgroup().getId())
			.set(TASK.SOURCE, task.getSource().value())
			.set(TASK.SOURCE_ID, task.getSourceId())
			.set(TASK.PROJECT, task.getProject().getId())
			.set(TASK.REGISTRY, task.getRegistry().getId())
			.set(TASK.ACTIVITY_TYPE, task.getActivityType())
			.set(TASK.SENDER, task.getSender().getId())
			.set(TASK.COMMENTS, task.getDescription())
			.set(TASK.REPEAT_PERIOD, task.getRepeatPeriod().value())
			.set(TASK.GTASK_ID, task.getGtaskId())
			.set(TASK.GTASKLIST_ID, task.getGtasklistId())
			.set(TASK.PARENT, task.getParent())
			.set(TASK.MODIFICATION_USER, ctx.getUser())
			.set(TASK.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.where(TASK.ID.eq(task.getId())).execute();
		return task;
	}
	
	public static Task insert(AONContext ctx, Task task) {
		task.setNumber(getLastTaskNumber(ctx,task));
		Integer id = ctx.getDslContext().insertInto(TASK)
			.set(TASK.ACTIVITY_TYPE, task.getActivityType())
			.set(TASK.COMMENTS, task.getDescription())
			.set(TASK.DESCRIPTION, task.getTitle())
			.set(TASK.DOMAIN, task.getDomain())
			.set(TASK.DUE_DATE,  AonDateUtils.toTimestamp(new Date()))
			.set(TASK.END_DATE,  AonDateUtils.toTimestamp(new Date()))
			.set(TASK.GTASK_ID, task.getGtaskId())
			.set(TASK.GTASKLIST_ID, task.getGtasklistId())
			.set(TASK.NUMBER, task.getNumber())
			.set(TASK.PERCENT, task.getPercent())
			.set(TASK.PRIORITY, task.getPriority().value())
			.set(TASK.PROJECT, task.getProject().getId())
			.set(TASK.REGISTRY, task.getRegistry().getId())
			.set(TASK.REPEAT_PERIOD, task.getRepeatPeriod().value())
			.set(TASK.SENDER, task.getSender().getId())
			.set(TASK.SOURCE, task.getSource().value())
			.set(TASK.START_DATE,  AonDateUtils.toTimestamp(new Date()))
			.set(TASK.STATUS, task.getStatus().value())
			.set(TASK.TASK_HOLDER, task.getTaskHolder().getId())
			.set(TASK.WORKGROUP, task.getWorkgroup().getId())
			.set(TASK.CREATION_USER, ctx.getUser())
			.set(TASK.CREATION_DATE,  AonDateUtils.toTimestamp(new Date()))
			.set(TASK.MODIFICATION_USER, ctx.getUser())
			.set(TASK.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.returning(TASK.ID).fetchOne().getId();
		return task.setId(id);
	}	

	public static void delete(AONContext ctx, Integer id){
		delete(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getIdProperty().eq(id)));
	}
	
	public static void delete(AONContext ctx, TaskFilter filter){
		ctx.getDslContext().delete(TASK)
		.where(TASK_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	private static Integer getLastTaskNumber(AONContext ctx, Task task) {
		Integer number = ctx.getDslContext()
				.select(DSL.max(TASK.NUMBER))
				.from(TASK)
				.where(TASK.DOMAIN.eq(ctx.getDomainId()))
				.fetchOne().value1();
		return (number!=null ? number : 0) + 1;
	}
	
	public static class TaskFiller implements Function<Record, Task> {

		@Override
		public Task apply(Record r) {
			return new Task()
				.setId(r.getValue(TASK.ID))
				.setDomain(r.getValue(TASK.DOMAIN))
				.setActivityType(r.getValue(TASK.ACTIVITY_TYPE))
				.setDescription(r.getValue(TASK.COMMENTS))
				.setTitle(r.getValue(TASK.DESCRIPTION))
				.setDueDate(r.getValue(TASK.DUE_DATE))
				.setEndDate(r.getValue(TASK.END_DATE))
				.setGtaskId(r.getValue(TASK.GTASK_ID))
				.setGtasklistId(r.getValue(TASK.GTASKLIST_ID))
				.setPercent(r.getValue(TASK.PERCENT))
				.setPriority(Priority.safeValueOf(r.getValue(TASK.PRIORITY)))
				.setProject(new Project().setId(r.getValue(TASK.PROJECT)))
				.setRegistry(new Registry().setId(r.getValue(TASK.REGISTRY)))
				.setRepeatPeriod(TaskPeriod.safeValueOf(r.getValue(TASK.REPEAT_PERIOD)))
				.setSender((TaskHolder) new TaskHolder().setId(r.getValue(TASK.SENDER)))
				.setSource(TaskSource.safeValueOf(r.getValue(TASK.SOURCE)))
				.setSourceId(r.getValue(TASK.SOURCE_ID))
				.setStartDate(r.getValue(TASK.START_DATE))
				.setStatus(TaskStatus.safeValueOf(r.getValue(TASK.STATUS)))
				.setTaskHolder((TaskHolder) new TaskHolder().setId(r.getValue(TASK.TASK_HOLDER)))
				.setWorkgroup(new Workgroup().setId(r.getValue(TASK.WORKGROUP)))
				.setNumber(r.getValue(TASK.NUMBER))
				.setCreationUser(r.getValue(TASK.CREATION_USER))
				.setCreationDate(r.getValue(TASK.CREATION_DATE))
				.setModificationUser(r.getValue(TASK.MODIFICATION_USER))
				.setModificationDate(r.getValue(TASK.MODIFICATION_DATE))
				.setParent(r.getValue(TASK.PARENT));
		}
	}
}
