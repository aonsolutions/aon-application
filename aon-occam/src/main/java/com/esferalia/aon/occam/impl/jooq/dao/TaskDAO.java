package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;
import static com.esferalia.aon.jooq.tables.TaskWorkflow.TASK_WORKFLOW;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.InsertSetStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Select;
import org.jooq.SelectConditionStep;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.SelectSelectStep;
import org.jooq.UpdateSetMoreStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.jooq.tables.records.TaskTagRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskCounts;
import com.esferalia.aon.occam.api.model.task.TaskEvaluation;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskPeriod;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;
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
	private static final Registry SENDER_REGISTRY = REGISTRY.as("registry_sender");
	private static final com.esferalia.aon.jooq.tables.TaskHolder SENDER = TASK_HOLDER.as("sender");
	
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
		@Override public Property<Byte> getEvaluationProperty() {return new FilterDAO.PropertyDAO<>(TASK.EVALUATION);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<>(TASK.TASK_HOLDER);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<>(TASK.WORKGROUP);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(TASK.NUMBER);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.MODIFICATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(TASK.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(TASK.CREATION_DATE);}
		@Override public Property<Integer> getParentProperty() {return new FilterDAO.PropertyDAO<>(TASK.PARENT);}
		
		@Override public Property<Integer> getTagIdProperty(){return new FilterDAO.PropertyDAO<>(TAG.ID);}
		@Override public Property<String> getTagNameProperty(){return new FilterDAO.PropertyDAO<>(TAG.NAME);}
		@Override public Property<String> getCommentsWorkflowProperty(){return new FilterDAO.PropertyDAO<>(TASK_WORKFLOW.COMMENT);}
		@Override public Property<String> getTaskHolderNameProperty(){return new FilterDAO.PropertyDAO<>(TH_REGISTRY.NAME);}
	}
	
	/**
	 * Create tag not exist
	 * @param ctx
	 * @param task
	 */
	
	private static <T extends Record> SelectOnConditionStep<T> selects(SelectSelectStep<T> select) {
		return select
		.from(TASK)
		.innerJoin(DOMAIN).on(DOMAIN.ID.eq(TASK.DOMAIN))
		.leftOuterJoin(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(TASK.TASK_HOLDER))
		.leftOuterJoin(WORKGROUP).on(WORKGROUP.ID.eq(TASK.WORKGROUP))
		.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(TASK.REGISTRY))
		.leftOuterJoin(TH_REGISTRY).on(TH_REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
		.leftOuterJoin(SENDER).on(SENDER.REGISTRY.eq(TASK.SENDER))
		.leftOuterJoin(SENDER_REGISTRY).on(SENDER_REGISTRY.ID.eq(SENDER.REGISTRY))
		.leftOuterJoin(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
		.leftOuterJoin(TAG).on(TAG.ID.eq(TASK_TAG.TAG))
		.leftOuterJoin(TASK_WORKFLOW).on(TASK_WORKFLOW.TASK.eq(TASK.ID))
		;
	}
	

	public static Stream<Task> getStream(AONContext ctx, TaskFilter filter){	
		System.out.println("getStream");
		return getStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<Task> getStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage){	
		System.out.println("getStream page perPage");
		return getStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Stream<Task> getParentOrChildStream(AONContext ctx, TaskFilter filter){	
		return getParentOrChildStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Stream<Task> getParentOrChildStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage){	
		return getParentOrChildStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Task getTaskAndChilds(AONContext ctx, TaskFilter filter) {
		System.out.println("getTaskAndChilds");
		ctx.checkRead();

		Task task = getTaskAndChildsStream(ctx, filter).findFirst().orElse(new Task());
		if(task.getId() != null) {
			task.setWorkflows(TaskWorkflowDAO.getList(ctx, f -> f.getTaskProperty().eq(task.getId())));
		}
		return task;
	}
	
	public static Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage) {
		System.out.println("getTaskAndChildsStream page perPage");
		ctx.checkRead();
		return getTaskAndChildsStream(ctx, filter, Optional.of(page), Optional.of(perPage));
	}
	
	public static Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter) {
		ctx.checkRead();
		return getTaskAndChildsStream(ctx, filter, Optional.empty(), Optional.empty());
	}
	
	public static Task get(AONContext ctx, TaskFilter filter) {
		ctx.checkRead();

		Task task = getStream(ctx, filter).findFirst().orElse(new Task());
		if(task.getId() != null) {
			task.setWorkflows(TaskWorkflowDAO.getList(ctx, f -> f.getTaskProperty().eq(task.getId())));
		}
		
		return task;
	}
	
	private static Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter, Optional<Integer> page, Optional<Integer> perPage) {

		List<Task> tasks    = getStream(ctx, filter, page, perPage).collect(Collectors.toList());
		
		Integer[] parentIds = tasks.stream().map(Task::getId).toArray(Integer[]::new);
		
		if(parentIds!=null && parentIds.length>0) {
			
			List<Task> childs = getStream(ctx, f-> f.getParentProperty().in(parentIds) ).collect(Collectors.toList());

			tasks.forEach(task->{
				childs.stream()
				.filter(t-> t.getParent().equals(task.getId()))
				.sorted(Comparator.comparing(Task::getId))
				.forEach(task::addChild);
			});
		}
		
		return tasks.stream();
	}

	public static Task save(AONContext ctx, Task task) {
		TaskAutoComplete.autoComplete(ctx, task);
		TaskValidation.validate(ctx, task);
		if(task.getId() != null) {
			update(ctx, task);
		} else {
			insert(ctx, task);
		}
		saveTags(ctx, task);
		return task;
	}
	
	private static Task update(AONContext ctx, Task task) {
		UpdateSetMoreStep<TaskRecord> sets = ctx.getDslContext()
		.update(TASK)
		.set(TASK.DESCRIPTION, task.getTitle())
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
		.set(TASK.GTASKLIST_ID, task.getGtasklistId())
		.set(TASK.PARENT, task.getParent())
		.set(TASK.MODIFICATION_USER, ctx.getUser())
		.set(TASK.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()));
		
		task.getGtaskId().ifPresent(d-> sets.set(TASK.GTASK_ID, d));

		if(task.getEvaluation()!=null) {
			sets.set(TASK.EVALUATION, task.getEvaluation().value());
		}
			
		sets.where(TASK.ID.eq(task.getId())).execute();

		updateChildsGrouped(ctx, task);			
		
		ctx.log().debug("UPDATE TASK id: " + task.getId());		
		return task;
	}
	
	private static Task insert(AONContext ctx, Task task) {
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
			 TASK.PARENT, 
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
		)
		.select(getLastTaskNumber(task, ctx))
		.returning(TASK.ID, TASK.SOURCE, TASK.NUMBER).fetchOne();
		
		task.setId(r.getValue(TASK.ID));
		task.setSource(TaskSource.safeValueOf(r.getValue(TASK.SOURCE)));
		task.setNumber(r.getValue(TASK.NUMBER));

		updateChildsGrouped(ctx, task);			
	
		ctx.log().debug("INSERT TASK id: " + task.getId());	
		return task;
	}	
	
	private static void saveTags(AONContext ctx, Task task) {
		DSLContext dslContext = ctx.getDslContext();
		InsertSetMoreStep<TaskTagRecord> insertTaskTags = null;
		
		setTagIdOrSave(ctx, task);
		
		List<Tag> tags = task.getTags().stream().filter(t->t.getId()!=null).collect(Collectors.toList());
		
		for(Tag tag: tags) {
			
			Optional<TaskTagRecord> tagExist = dslContext.select()
			.from(TASK_TAG)
			.where(TASK_TAG.TASK.eq(task.getId()))
			.and(TASK_TAG.TAG.eq(tag.getId()))
			.fetchStreamInto(TASK_TAG)
			.findFirst();
			
			if(!tagExist.isPresent()) {
				InsertSetStep<TaskTagRecord> insert = null != insertTaskTags ? insertTaskTags.newRecord() : dslContext.insertInto(TASK_TAG);
						
				InsertSetMoreStep<TaskTagRecord> recordSets = insert
				.set(TASK_TAG.DOMAIN, task.getDomain().getId())
				.set(TASK_TAG.TASK, task.getId())
				.set(TASK_TAG.TAG, tag.getId())
				;
				
				insertTaskTags = recordSets;
			}
		}	
		
		if(null!=insertTaskTags) {
			insertTaskTags.execute();
		}
		
		deleteTags(ctx, task);
	}
	
	private static Stream<Task> getStream(AONContext ctx, TaskFilter filter, Optional<Integer> page, Optional<Integer> perPage){	
		SelectHavingStep<Record> query = selects(getFields(ctx.getDslContext())) 
		.where(
			TASK.ID.in( 
				getTaskIds(ctx, page, perPage, TASK_PROPERTIES.getConditions(filter))
			)
		)
		.groupBy(TASK.ID, TAG.ID, DOMAIN.ID);
	
		System.out.println("getStream "+page+" "+perPage);
			
	    Map<Task, List<Tag>> taskMaps = query.fetchGroups(new TaskFiller()::apply, new TagFiller()::apply);
		
		taskMaps.forEach((task, tags) -> tags.forEach(task::addTag) );
		 
		return taskMaps.keySet().stream().sorted((e1, e2) -> e1.getCreationDate().compareTo(e2.getCreationDate()));
	}
	
	private static Stream<Task> getParentOrChildStream(AONContext ctx, TaskFilter filter, Optional<Integer> page, Optional<Integer> perPage){	
		SelectHavingStep<Record> query = selects( getFields(ctx.getDslContext()) )
		.where(
			TASK.ID.in( 
				getTaskIds(ctx, page, perPage, whereParentOrChild(ctx, filter))
			)
		)
		.groupBy(TASK.ID, TAG.ID, DOMAIN.ID);
	   
		System.out.println("getParentOrChildStream "+page+" "+perPage);
		
		System.out.println(query.getSQL());
		
		Map<Task, List<Tag>> taskMaps = query.fetchGroups(new TaskFiller()::apply, new TagFiller()::apply);
		
		taskMaps.forEach((task, tags) -> tags.forEach(task::addTag) );

		List<Task> tasks = taskMaps.keySet().stream().filter(distinctByKey(Task::getId)).collect(Collectors.toList());
		
		setParent(ctx, tasks);

		return taskMaps.keySet().stream().sorted((e1, e2) -> e1.getCreationDate().compareTo(e2.getCreationDate()));
	}
	
	private static SelectSelectStep<Record> getFields(DSLContext ctx) {
		return ctx
		.select(TASK.fields())
		.select(DOMAIN.fields())
		.select(TASK_HOLDER.fields())
		.select(WORKGROUP.fields())
		.select(REGISTRY.fields())
		.select(TH_REGISTRY.fields())
		.select(SENDER.fields())
		.select(SENDER_REGISTRY.fields())
		.select(TASK_TAG.TASK)
		.select(TAG.fields());
	}
	
	private static void setTagIdOrSave(AONContext ctx, Task task) {
		DSLContext dslContext = ctx.getDslContext();

		List<Tag> tagNotId = task.getTags().stream().filter(tag->tag.getId()==null&& tag.getName()!=null).collect(Collectors.toList());
		
		for (Tag tag: tagNotId) {
			String name = tag.getName().toUpperCase();
			
			Optional<TagRecord> tagExist = dslContext.select()
			.from(TAG)
			.where(TAG.DOMAIN.eq(task.getDomain().getId()))
			.and(DSL.upper(TAG.NAME).eq(name))
			.and(TAG.TYPE.eq(tag.getTagType().value()))
			.fetchStreamInto(TAG)
			.findFirst();
			
			Integer tagId = null;
			
			if(tagExist.isPresent()) {
				TagRecord r = tagExist.get();
				tagId = r.getId();
				if(tag.getColor()!=null) {
					r.set(TAG.COLOR, tag.getColor());
					r.update();
				}
			} else {
				InsertSetMoreStep<TagRecord> condition = dslContext
				.insertInto(TAG)
				.set(TAG.DOMAIN, task.getDomain().getId())
				.set(TAG.NAME, tag.getName())
				.set(TAG.TYPE, tag.getTagType().value());
				if(tag.getColor()!=null) {					
					condition.set(TAG.COLOR, tag.getColor());
				}
				
				tagId = condition.returning(TAG.ID).fetchOne().getId();
			}
			
			tag.setId(tagId);
		}
	}

	public static void delete(AONContext ctx, Integer id){
		TaskAttachDAO.deleteByTask(ctx, id);
		TaskWorkflowDAO.deleteByTask(ctx, id);	
		TaskOldDAO.deleteTaskEvent(ctx, f -> f.getTaskProperty().eq(id));
		TaskOldDAO.deleteTaskComment(ctx, f -> f.getTaskProperty().eq(id));
		TaskOldDAO.deleteTaskTag(ctx, f -> f.getTaskProperty().eq(id));
		DailyTrackingDAO.delete(ctx, f -> f.getTaskProperty().eq(id));
		delete(ctx, f -> f.getIdProperty().eq(id).or(f.getParentProperty().eq(id)));
		ctx.log().debug("DELETE TASK id:" + id);
	}
	
	private static void delete(AONContext ctx, TaskFilter filter) {
		ctx.getDslContext().delete(TASK).where(TASK_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static Map<String, Integer> getTaskCount(AONContext ctx, TaskFilter sender, TaskFilter receiver){
		Integer sd = 0;
		Integer rv = 0;
	
		HashMap<String, Integer> map = new HashMap<>();
		
		//SENDER
		sd  = selectCount(ctx, sender).fetchOne(0, Integer.class);
		
		//RECEIVED
		rv  = selectCount(ctx, receiver).fetchOne(0, Integer.class);

		if(sd==null)  sd = 0;
		if(rv==null) rv = 0;
	
		map.put("sender", sd);
		map.put("task_holder", rv);
		
		return map;
	}
	
	public static Integer getTaskCountFilter(AONContext ctx, TaskFilter taskFilter){
		Collection<Condition> whereConditions = new ArrayList<>();
		whereConditions.addAll(Arrays.asList(TASK_PROPERTIES.getConditions(taskFilter)));		
		return ctx.getDslContext().select(DSL.count())
		.from(TASK)
		.where(whereConditions).fetchOne(0, int.class);
	}
	
	
	public static TaskCounts getTaskGeneralCount(AONContext ctx, Optional<TaskFilter> status, Optional<TaskFilter> workgroup, Optional<TaskFilter> tags){
		TaskCounts taskCounts = new TaskCounts();
		String count = "count";
		
		status.ifPresent(filter->
			ctx.getDslContext()
			.select(DSL.count(TASK.ID).as(DSL.name(count)), TASK.STATUS)
			.from(TASK)
			.where(TASK_PROPERTIES.getConditions(filter))
			.groupBy(TASK.STATUS)
			.fetch()
			.forEach(r->{
				taskCounts.addStatus(TaskStatus.safeValueOf(r.get(TASK.STATUS)), (Integer) r.get(DSL.name(count)));
			})
		);
		
		workgroup.ifPresent(filter->
			ctx.getDslContext()
			.select(DSL.count(TASK.ID).as(DSL.name(count)), TASK.WORKGROUP)
			.from(TASK)
			.where(TASK_PROPERTIES.getConditions(filter))
			.groupBy(TASK.WORKGROUP)
			.fetch()
			.forEach(r->  {
				String wg = r.get(TASK.WORKGROUP)!= null ? r.get(TASK.WORKGROUP).toString() : "true";
				taskCounts.addWorkgroup(wg, (Integer) r.get(DSL.name(count)));
			})
		);
		
		tags.ifPresent(filter->
			ctx.getDslContext()
			.select(DSL.count(TASK.ID).as(DSL.name(count)), TAG.ID)
			.from(TASK)
			.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
			.join(TAG).on(TAG.ID.eq(TASK_TAG.TAG))
			.where(TASK_PROPERTIES.getConditions(filter))
			.groupBy(TAG.ID)
			.fetch()
			.forEach(r->  {
				if(r.get(TAG.ID)!=null) {
					taskCounts.addTag(r.get(TAG.ID).toString(), (Integer) r.get(DSL.name(count)));
				}
			})
		);
		
		return taskCounts;
	}
	
	private static SelectConditionStep<Record1<Integer>> selectCount(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().selectCount().from(TASK).where(TASK_PROPERTIES.getConditions(filter));
	}	
	
	private static void deleteTags(AONContext ctx, Task task) {
		Integer[] idsTag = task.getTags().stream().map(Tag::getId).toArray(Integer[]::new);
		TaskOldDAO.deleteTaskTag(ctx, f -> f.getTaskProperty().eq(task.getId()).and(f.getTagProperty().notIn(idsTag)) );
	}
	
	private static Condition whereParentOrChild(AONContext ctx, TaskFilter filter) {
		Condition combined = DSL.trueCondition();
		for (Condition condition : TASK_PROPERTIES.getConditions(filter)) {			  
			combined = combined.and(condition);
		}

		return combined.and(TASK.PARENT.isNull())
		.or(
			TASK.ID.in(
				selects(ctx.getDslContext().select(TASK.PARENT))
    			.where(TASK_PROPERTIES.getConditions(filter))
    			.and(TASK.PARENT.isNotNull())
	    	).and(
	    		TASK.PARENT.isNull()
	    	)
		);
	}
	
	private static SelectConditionStep<Record> getLastTaskNumber(Task task, AONContext ctx) {
		 Optional<String> gtaskId = task.getGtaskId();
		 
		 return DSL.select( 
			DSL.val(task.getActivityType()),
			DSL.val(task.getDescription()),
			DSL.val(task.getTitle()),
			DSL.val(task.getDomain().getId()),
			DSL.val(AonDateUtils.toTimestamp(new Date())),
			DSL.val(AonDateUtils.toTimestamp(new Date())),
			DSL.val(gtaskId.isPresent() ? gtaskId.get() : null),
			DSL.val(task.getGtasklistId()),
			DSL.val(task.getPercent()),
			DSL.val(task.getParent()),
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
	
	private static void updateChildsGrouped(AONContext ctx, Task task) {
		if( task.getSource().equals(TaskSource.GROUPED) ) {
			List<Task> childs = task.getChilds();
			if(!childs.isEmpty()) {
				List<Task> childsOld = getStream(ctx,  f-> f.getParentProperty().eq(task.getId()) ).collect(Collectors.toList());
				
				updateParentChilds(ctx, null, childsOld);

				updateParentChilds(ctx, task, childs);
			}
		}
	}
	
	private static void updateParentChilds(AONContext ctx, Task parent, List<Task> childs) {
		if(!childs.isEmpty()) {
			Integer[] childIds = childs.stream().map(Task::getId).toArray(Integer[]::new);
			
			Integer parentId   = parent!=null ? parent.getId() : null;
			
			ctx.getDslContext()
			.update(TASK)
			.set(TASK.MODIFICATION_USER, ctx.getUser())
			.set(TASK.MODIFICATION_DATE, AonDateUtils.toTimestamp(new Date()))
			.set(TASK.PARENT, parentId)
			.where(TASK.ID.in(childIds))
			.execute();
		}
	}
	
	private static Integer[] getTaskIds(AONContext ctx, Optional<Integer> page, Optional<Integer> perPage, Condition ...condition) {		
		SelectConditionStep<Record1<Integer>> query = 
		selects(ctx.getDslContext().select(TASK.ID))
		.where(condition);
	
		if(page.isPresent() && perPage.isPresent()) {
			Integer per = perPage.get();
			Integer p = page.get();
			query.limit(per).offset(per * (p -1));
		}

	   return query.groupBy(TASK.ID, TAG.ID, DOMAIN.ID).orderBy(TASK.CREATION_DATE.desc()).fetch(TASK.ID).toArray(Integer[]::new);
	}
	
	public static class TaskFiller extends Filler implements Function<Record, Task> {

		public Task apply(Record r) {
			return build(r);
		}
				
		public static Task build(Record r) {
			return new Task()
				.setId(r.getValue(TASK.ID))
				.setDomain(checkField(r, DOMAIN.ID)
						? DomainFiller.build(r)
						: new Domain().setId(r.getValue(TASK.DOMAIN)))
				.setActivityType(r.getValue(TASK.ACTIVITY_TYPE))
				.setTitle(r.getValue(TASK.DESCRIPTION))
				.setDescription(r.indexOf(TASK.COMMENTS)!=-1 ? r.getValue(TASK.COMMENTS) : null)
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
				.setEvaluation(r.getValue(TASK.EVALUATION)!=null ? TaskEvaluation.safeValueOf(r.getValue(TASK.EVALUATION)) : null)
				.setModificationUser(r.getValue(TASK.MODIFICATION_USER))
				.setModificationDate(r.getValue(TASK.MODIFICATION_DATE))
				.setParent(r.getValue(TASK.PARENT));
		}
	}
	
	public static class TagFiller extends Filler implements Function<Record, Tag> {

		@Override
		public Tag apply(Record r) {
			return new Tag()
				.setId(r.getValue(TAG.ID))
				.setColor(r.getValue(TAG.COLOR))
				.setDomain(r.getValue(TAG.DOMAIN))
				.setName(r.getValue(TAG.NAME))
				.setTagType(TagType.safeValueOf(r.getValue(TAG.TYPE)))
			;
		}
	}
	
	private static void setParent(AONContext ctx, List<Task> tasks) {
		Integer[] parentIds  = tasks.stream().filter(Task::isParent).map(Task::getId).toArray(Integer[]::new);
		
		if(parentIds!=null && parentIds.length>0) {
			List<Task> childsAll = getTaskAndChildsStream(ctx,  f-> f.getParentProperty().in(parentIds) ).collect(Collectors.toList());

			tasks.forEach(task->{
				childsAll.stream()
				.filter(t-> t.getParent().equals(task.getId()))
				.sorted(Comparator.comparing(Task::getId))
				.forEach(task::addChild);
			});
		}
	}
	
	private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
