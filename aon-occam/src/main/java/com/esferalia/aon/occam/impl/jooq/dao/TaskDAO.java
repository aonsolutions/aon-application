package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStep1;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskPeriod;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO.RegistryFiller;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO.TaskHolderFiller;
import com.esferalia.aon.occam.impl.jooq.dao.WorkgroupDAO.WorkgroupFiller;
import com.esferalia.aon.occam.impl.jooq.validation.TaskAutoComplete;
import com.esferalia.aon.occam.impl.jooq.validation.TaskValidation;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskDAO {

	private TaskDAO() {
		throw new IllegalStateException("Utility Class");
	}
	
	private static final Registry TH_REGISTRY = REGISTRY.as("registry_task_holder");
	private static final com.esferalia.aon.jooq.tables.TaskHolder SENDER = TASK_HOLDER.as("sender");
	private static final Registry SENDER_REGISTRY = REGISTRY.as("registry_sender");
	
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TASK.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK.DOMAIN);}
		@Override public Property<Integer> getActivityTypeProperty() {return new FilterDAO.PropertyDAO<>(TASK.ACTIVITY_TYPE);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(TASK.COMMENTS);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(TASK.DESCRIPTION);}
		@Override public Property<Timestamp> getDueDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.DUE_DATE);}
		@Override public Property<Timestamp> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.END_DATE);}
		@Override public Property<String> getGtaskIdProperty() {return new FilterDAO.PropertyDAO<>(TASK.GTASK_ID);}
		@Override public Property<String> getGtasklisIdProperty() {return new FilterDAO.PropertyDAO<>(TASK.GTASKLIST_ID);}
		@Override public Property<Byte> getPercentProperty() {return new FilterDAO.PropertyDAO<>(TASK.PERCENT);}
		@Override public Property<Byte> getPriorityProperty() {return new FilterDAO.PropertyDAO<>(TASK.PRIORITY);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(TASK.PROJECT);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(TASK.REGISTRY);}
		@Override public Property<String> getRegistryNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<Byte> getRepeatPeriodProperty() {return new FilterDAO.PropertyDAO<>(TASK.REPEAT_PERIOD);}
		@Override public Property<Integer> getSenderProperty() {return new FilterDAO.PropertyDAO<>(TASK.SENDER);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(TASK.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(TASK.SOURCE_ID);}
		@Override public Property<Timestamp> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.START_DATE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(TASK.STATUS);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(TASK.TASK_HOLDER);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<>(TASK.WORKGROUP);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(TASK.NUMBER);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.MODIFICATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.CREATION_DATE);}
		@Override public Property<Integer> getParentProperty() {return new FilterDAO.PropertyDAO<>(TASK.PARENT);}
	}
	
	public static SelectSeekStep1<Record, Timestamp> select(AONContext ctx, TaskFilter filter){	
		return ctx.getDslContext()
				.select()
				.from(TASK)
				.innerJoin(DOMAIN).on(DOMAIN.ID.eq(TASK.DOMAIN))
				.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(TASK.WORKGROUP))
				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(TASK.REGISTRY))
				.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TASK.TASK_HOLDER))
				.leftOuterJoin(TH_REGISTRY).on(TH_REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
				.leftOuterJoin(SENDER).on(SENDER.REGISTRY.eq(TASK.SENDER))
				.leftOuterJoin(SENDER_REGISTRY).on(SENDER_REGISTRY.ID.eq(SENDER.REGISTRY))
				.where(TASK_PROPERTIES.getConditions(filter))
				.orderBy(TASK.CREATION_DATE.desc());
	}
	
	public static Stream<Task> getStream(AONContext ctx, TaskFilter filter){	
		return select(ctx, filter).fetch().stream().map(new TaskFiller());
	}
	
	public static Stream<Task> getStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage){	
		return select(ctx, filter)
			.limit(perPage)
			.offset(perPage * (page -1))
			.fetch().stream().map(new TaskFiller());
	}
	
	public static LinkedList<Task> getList(AONContext ctx, TaskFilter filter){	
		return getStream(ctx, filter).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static LinkedList<Task> getList(AONContext ctx, TaskFilter filter, Integer page, Integer perPage){	
		return getStream(ctx, filter, page, perPage).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static Task get(AONContext ctx, TaskFilter filter) {
		Task task = select(ctx, filter).limit(1)
			.stream().map(new TaskFiller())
			.findFirst().orElse(new Task());
		if(task.getId() != null) {
			task.setWorkflows(TaskWorkflowDAO.getList(ctx, f -> f.getTaskProperty().eq(task.getId())));
		}
		return task;
	}
	
	public static Task save(AONContext ctx, Task task) {
		TaskAutoComplete.autoComplete(ctx, task);
		TaskValidation.validate(ctx, task);
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
		ctx.log().debug("UPDATE TASK id: " + task.getId());		
		return task;
	}
	
	public static Task insert(AONContext ctx, Task task) {
		Record r  = ctx.getDslContext().insertInto(
					 TASK,
					 TASK.ACTIVITY_TYPE, 
					 TASK.COMMENTS, 
					 TASK.DESCRIPTION, 
					 TASK.DOMAIN, 
					 TASK.DUE_DATE, 
					 TASK.END_DATE, 
					 TASK.GTASK_ID, 
					 TASK.GTASKLIST_ID, 
					 TASK.PERCENT, 
					 TASK.PRIORITY,
					 TASK.PROJECT,
					 TASK.REGISTRY,
					 TASK.REPEAT_PERIOD,
					 TASK.SENDER,
					 TASK.SOURCE,
					 TASK.SOURCE_ID,
					 TASK.START_DATE,
					 TASK.STATUS,
					 TASK.TASK_HOLDER,
					 TASK.WORKGROUP,
					 TASK.CREATION_USER,
					 TASK.CREATION_DATE,
					 TASK.MODIFICATION_USER,
					 TASK.MODIFICATION_DATE,
					 TASK.NUMBER
				 ).select(getLastTaskNumber(task, ctx))
			.returning(TASK.ID, TASK.SOURCE, TASK.NUMBER).fetchOne();
		task.setId(r.getValue(TASK.ID));
		task.setSource(TaskSource.safeValueOf(r.getValue(TASK.SOURCE)));
		task.setNumber(r.getValue(TASK.NUMBER));
		
		ctx.log().debug("INSERT TASK id: " + task.getId());	
		return task;
	}	

	public static void delete(AONContext ctx, Integer id){
		TaskAttachDAO.deleteByTask(ctx, id);
		TaskWorkflowDAO.deleteByTask(ctx, id);	
		TaskOldDAO.deleteTaskEvent(ctx, f -> f.getTaskProperty().eq(id));
		TaskOldDAO.deleteTaskComment(ctx, f -> f.getTaskProperty().eq(id));
		TaskOldDAO.deleteTaskTag(ctx, f -> f.getTaskProperty().eq(id));
		delete(ctx, f -> f.getIdProperty().eq(id));
		ctx.log().debug("DELETE TASK id:" + id);
	}
	
	private static void delete(AONContext ctx, TaskFilter filter) {
		ctx.getDslContext()
			.delete(TASK)
			.where(TASK_PROPERTIES.getConditions(filter))
			.execute();
	}
	
	public static HashMap<Byte, Integer> getTaskStatusCount(AONContext ctx, TaskFilter filter){
		HashMap<Byte, Integer> map = new HashMap<>();
		ctx.getDslContext()
		.select(DSL.count(TASK.STATUS).as(DSL.name("count")), TASK.STATUS)
		.from(TASK)
		.where(TASK_PROPERTIES.getConditions(filter))
		.groupBy(TASK.STATUS)
		.fetch().stream().forEach(r-> map.put(r.get(TASK.STATUS), (Integer) r.get(DSL.name("count"))));
		return map;
	}
	
	public static HashMap<String, Integer> getTaskCount(AONContext ctx, TaskFilter filter, Integer taskHolderId, Optional<String> email){
		Integer sender = 0;
		Integer taskHolder = 0;
		HashMap<String, Integer> map = new HashMap<>();
		
		sender = ctx.getDslContext().select(DSL.count(TASK.SENDER), TASK.SENDER).from(TASK)
				.where(TASK_PROPERTIES.getConditions(filter))
				.and(email.isPresent() ? TASK.GTASK_ID.eq(email.get()) :  TASK.SENDER.eq(taskHolderId))
				.groupBy(email.isPresent() ? TASK.GTASK_ID : TASK.SENDER)
				.fetchOne(0, Integer.class);
		
		if(!email.isPresent()) {
			taskHolder = ctx.getDslContext().select(DSL.count(TASK.TASK_HOLDER), TASK.TASK_HOLDER).from(TASK)
					.where(TASK_PROPERTIES.getConditions(filter))
					.and(TASK.TASK_HOLDER.eq(taskHolderId))
					.groupBy(TASK.TASK_HOLDER)
					.fetchOne(0, Integer.class);
		}
				
		if(sender==null)     sender = 0;
		if(taskHolder==null) taskHolder = 0;
	
		map.put("sender", sender);
		map.put("task_holder", taskHolder);
		
		return map;
	}
	
	private static SelectConditionStep<Record> getLastTaskNumber(Task task, AONContext ctx) {
		 return DSL.select( 
						DSL.val(task.getActivityType()),
						DSL.val(task.getDescription()),
						DSL.val(task.getTitle()),
						DSL.val(task.getDomain().getId()),
						DSL.val(AonDateUtils.toTimestamp(new Date())),
						DSL.val(AonDateUtils.toTimestamp(new Date())),
						DSL.val(task.getGtaskId()),
						DSL.val(task.getGtasklistId()),
						DSL.val(task.getPercent()),
						DSL.val(task.getPriority().value()),
						DSL.val(task.getProject().getId()),
						DSL.val(task.getRegistry().getId()),
						DSL.val(task.getRepeatPeriod().value()),
						DSL.val(task.getSender().getId()),
						DSL.val(task.getSource().value()),
						DSL.val(task.getSourceId()),
						DSL.val(AonDateUtils.toTimestamp(new Date())),
						DSL.val(task.getStatus().value()),
						DSL.val(task.getTaskHolder().getId()),
						DSL.val(task.getWorkgroup().getId()),
						DSL.val(ctx.getUser()),
						DSL.val( AonDateUtils.toTimestamp(new Date())),
						DSL.val(ctx.getUser()),
						DSL.val(AonDateUtils.toTimestamp(new Date())),
					   	DSL.coalesce(
							DSL.max(TASK.NUMBER), DSL.inline(0)
						).plus(DSL.inline(1))
				  )
				 .from(TASK)
				 .where(TASK.DOMAIN.eq(task.getDomain().getId()));
	}
	
	public static class TaskFiller extends Filler implements Function<Record, Task> {

		@Override
		public Task apply(Record r) {
			return new Task()
				.setId(r.getValue(TASK.ID))
				.setDomain(checkField(r, DOMAIN.ID)
						? DomainFiller.build(r)
						: new Domain().setId(r.getValue(TASK.DOMAIN)))
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
				.setRepeatPeriod(TaskPeriod.safeValueOf(r.getValue(TASK.REPEAT_PERIOD)))
				.setSender(checkField(r, SENDER.REGISTRY)
						? TaskHolderFiller.build(r, SENDER, SENDER_REGISTRY)
						: new TaskHolder().setRegistry(r.getValue(TASK.SENDER)))
				.setSource(TaskSource.safeValueOf(r.getValue(TASK.SOURCE)))
				.setSourceId(r.getValue(TASK.SOURCE_ID))
				.setStartDate(r.getValue(TASK.START_DATE))
				.setStatus(TaskStatus.safeValueOf(r.getValue(TASK.STATUS)))
				.setTaskHolder(r.getValue(TASK.TASK_HOLDER)!=null ?TaskHolderFiller.build(r, TH_REGISTRY) : new TaskHolder() )
				.setRegistry(RegistryFiller.build(r, REGISTRY))
				.setWorkgroup(WorkgroupFiller.build(r))
				.setNumber(r.getValue(TASK.NUMBER))
				.setCreationUser(r.getValue(TASK.CREATION_USER))
				.setCreationDate(r.getValue(TASK.CREATION_DATE))
				.setModificationUser(r.getValue(TASK.MODIFICATION_USER))
				.setModificationDate(r.getValue(TASK.MODIFICATION_DATE))
				.setParent(r.getValue(TASK.PARENT));
		}
	}
}
