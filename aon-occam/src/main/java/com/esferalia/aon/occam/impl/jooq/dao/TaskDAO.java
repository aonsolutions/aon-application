package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;
import static com.esferalia.aon.jooq.tables.TaskEvent.TASK_EVENT;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.SortField;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.jooq.tables.records.TaskCommentRecord;
import com.esferalia.aon.jooq.tables.records.TaskEventRecord;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskEventFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskEventProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskTagProperties;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.TagType;

public class TaskDAO {
	
	private static final TaskPropertiesDAO TASK_PROPERTIES = new TaskPropertiesDAO();
	private static final TaskTagPropertiesDAO TASK_TAG_PROPERTIES = new TaskTagPropertiesDAO();
	private static final TaskEventPropertiesDAO TASK_EVENT_PROPERTIES = new TaskEventPropertiesDAO();

	protected static class TaskTagPropertiesDAO implements TaskTagProperties {
		protected Condition[] getConditions(TaskTagFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_TAG.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_TAG.DOMAIN);}
		@Override public Property<Integer> getTagProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_TAG.TAG);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_TAG.TASK);}
		
	}
	
	protected static class TaskEventPropertiesDAO implements TaskEventProperties {
		protected Condition[] getConditions(TaskEventFilter filter) {	
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_EVENT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_EVENT.DOMAIN);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_EVENT.TASK);}
		@Override public Property<String> getEventProperty() {return new FilterDAO.PropertyDAO<String>(TASK_EVENT.EVENT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK_EVENT.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK_EVENT.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK_EVENT.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK_EVENT.MODIFICATION_DATE);}
	}
	
	protected static class TaskPropertiesDAO implements TaskProperties {
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
		@Override public Property<Timestamp> getStartDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK.START_DATE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.STATUS);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.TASK_HOLDER);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.WORKGROUP);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.NUMBER);}
	}
	
	public static Task getTask(AONContext ctx, TaskFilter filter){
		return ctx.getDslContext().select().from(TASK).where(TASK_PROPERTIES.getConditions(filter)).fetchInto(TASK)
				.stream().map(new FullTaskFiller()).findFirst().orElse(new Task());
	}
	
	public static Integer getLastTaskNumber(AONContext ctx){
		return ctx.getDslContext()
			.select(DSL.max(TASK.NUMBER))
			.from(TASK)
			.where(TASK.DOMAIN.eq(ctx.getDomainId()))
			.fetchOne().value1();
	}
	
	public static Integer getCommentsCount(AONContext ctx, Integer taskId){
		return ctx.getDslContext()
			.select(DSL.count(TASK_COMMENT.ID))
			.from(TASK_COMMENT)
			.where(TASK_COMMENT.TASK.eq(taskId))
			.fetchOne().value1();
	}
	
	public static Stream<TaskComment> getTaskCommentStream(AONContext ctx, Integer taskId){
		return ctx.getDslContext()
			.select()
			.from(TASK_COMMENT)
			.where(TASK_COMMENT.TASK.eq(taskId))
			.orderBy(TASK_COMMENT.ID.desc())
			.fetchInto(TASK_COMMENT).stream().map(new FullTaskCommentFiller());
	}
	
	public static Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId){
		return ctx.getDslContext()
			.select()
			.from(TASK_EVENT)
			.where(TASK_EVENT.TASK.eq(taskId))
			.orderBy(TASK_EVENT.ID.desc())
			.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller());
	}

	public static Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter){
		return ctx.getDslContext().select().from(TAG).join(TASK_TAG).on(TAG.ID.eq(TASK_TAG.TAG))
			.where(TASK_TAG_PROPERTIES.getConditions(filter)).fetchInto(TAG).stream().map(new FullTagFiller());
	}
	
	public static Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter){
		// state 
		Condition c;
		Boolean tagBool = false;
		
		if(issueFilter.getState().equals("open")) c = TASK.STATUS.eq(TaskStatus.PENDING.value())
												.or(TASK.STATUS.eq(TaskStatus.IN_PROGRESS.value()));
		else if(issueFilter.getState().equals("closed")) c = TASK.STATUS.eq(TaskStatus.FINISHED.value());
		else if(issueFilter.getState().equals("deleted")) c = TASK.STATUS.eq(TaskStatus.DELETED.value());
		else c = TASK.STATUS.ne(TaskStatus.DELETED.value());
		// assignee
		if(issueFilter.getAssignee() != null && !issueFilter.getAssignee().equals(""))
			c = c.and(TASK.TASK_HOLDER.eq(Integer.parseInt(issueFilter.getAssignee())));

		// creator
		if(issueFilter.getCreator() != null && !issueFilter.getCreator().equals(""))
			c = c.and(TASK.CREATION_USER.eq(issueFilter.getCreator())); 
		
		// labels
		if(issueFilter.getLabels() != null && !issueFilter.getLabels().equals("")){
			tagBool = true;
			c = c.and(TASK_TAG.TAG.eq(Integer.parseInt(issueFilter.getLabels())));	
			/*String[] labels = issueFilter.getLabels().split(",");
			for (String label : labels){
				//c = c.and(TAG.NAME.eq(label));
			}*/
		}
		
		// mentioned
		if(issueFilter.getMentioned() != null && !issueFilter.getMentioned().equals("")){}
		
		// milestone
		if(issueFilter.getMilestone() != null && !issueFilter.getMilestone().equals("")){}
		
		// since
		if(issueFilter.getSince() != null && !issueFilter.getSince().equals("")){}
		
		// sort    created | updated | comments
		TableField<TaskRecord, Timestamp> sort = TASK.START_DATE; 
 		if(issueFilter.getSort() != null && !issueFilter.getSort().equals("updated")){
 			//**** sort = TASK.UPDATE_DATE;
		}
 		
		// direction
 		SortField<Timestamp> sortDir = sort.desc();
		if(issueFilter.getDirection() != null && !issueFilter.getDirection().equals("asc")){
			sortDir = sort.asc();
		}
		
		// title
		if(issueFilter.getTitle() != null && !issueFilter.getTitle().equals(""))
			c = c.and(TASK.DESCRIPTION.contains(issueFilter.getTitle()))
				.or(TASK.COMMENTS.contains(issueFilter.getTitle()));	
		
		if(issueFilter.getType() != null && !issueFilter.getType().equals("")){
			tagBool = true;
			c = c.and(TASK_TAG.TAG.eq(Integer.parseInt(issueFilter.getType())));	
		}
		
		//priority
		if(issueFilter.getPriority() != null && !issueFilter.getPriority().equals(""))
			c = c.and(TASK.PRIORITY.eq(Priority.valueNameOf(issueFilter.getPriority()).value()));
						
		if(tagBool){
			return ctx.getDslContext().selectDistinct().from(TASK).join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(c).and(TASK.NUMBER.isNotNull()).orderBy(sortDir)
					.limit(issueFilter.getPerPage())
					.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
					.fetchInto(TASK).stream().map(new FullTaskFiller());
		}
		return ctx.getDslContext().select().from(TASK) 
				.where(TASK_PROPERTIES.getConditions(filter)).and(c).and(TASK.NUMBER.isNotNull()).orderBy(sortDir)
				.limit(issueFilter.getPerPage())
				.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
				.fetchInto(TASK).stream().map(new FullTaskFiller());
	}

	public static Integer createTask(AONContext ctx, Task task) {
		return ctx.getDslContext().insertInto(TASK, TASK.ACTIVITY_TYPE, TASK.COMMENTS, TASK.DESCRIPTION, TASK.DOMAIN, TASK.DUE_DATE, TASK.END_DATE, TASK.GTASK_ID, TASK.GTASKLIST_ID,TASK.NUMBER,
				TASK.PERCENT, TASK.PRIORITY, TASK.PROJECT, TASK.REGISTRY, TASK.REPEAT_PERIOD, TASK.SENDER, TASK.SOURCE, TASK.START_DATE, TASK.STATUS, TASK.TASK_HOLDER, TASK.MODIFICATION_DATE, TASK.WORKGROUP,
				TASK.CREATION_USER, TASK.CREATION_DATE, TASK.MODIFICATION_USER)
			.values(task.getActivityType(),task.getComments(), task.getDescription(), task.getDomain(), task.toTimestamp(task.getDueDate()), task.toTimestamp(task.getEndDate()), task.getGtaskId(), task.getGtasklistId(), task.getNumber(),
				task.getPercent(), task.getPriority(), task.getProject(), task.getRegistry(), task.getRepeatPeriod(), task.getSender(), task.getSource(), task.toTimestamp(task.getStartDate()), task.getStatus(), task.getTaskHolder(), task.toTimestamp(task.getModificationDate()), task.getWorkgroup(),
				task.getCreationUser(), task.toTimestamp(task.getCreationDate()), task.getModificationUser())
			.returning(TASK.ID).fetchOne().getId();
	}
	
	public static void updateTaskStatus(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)
			.set(TASK.STATUS, task.getStatus())
			.set(TASK.END_DATE,task.toTimestamp(task.getEndDate()))
			.set(TASK.MODIFICATION_USER, task.getModificationUser())
			.set(TASK.MODIFICATION_DATE, task.toTimestamp(task.getModificationDate()))
			.where(TASK.ID.eq(task.getId())).execute();
	}
	
	public static void updateTaskDescription(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)
			.set(TASK.COMMENTS, task.getComments())
			.set(TASK.MODIFICATION_USER, task.getModificationUser())
			.set(TASK.MODIFICATION_DATE, task.toTimestamp(task.getModificationDate()))
			.where(TASK.ID.eq(task.getId())).execute();
	}
	
	public static void updateTaskPriority(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)
			.set(TASK.PRIORITY, task.getPriority())
			.set(TASK.MODIFICATION_USER, task.getModificationUser())
			.set(TASK.MODIFICATION_DATE, task.toTimestamp(task.getModificationDate()))
			.where(TASK.ID.eq(task.getId())).execute();
	}
	
	public static void updateTaskUser(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)			
			.set(TASK.TASK_HOLDER, task.getTaskHolder())
			.set(TASK.MODIFICATION_USER, task.getModificationUser())
			.set(TASK.MODIFICATION_DATE, task.toTimestamp(task.getModificationDate()))
			.set(TASK.WORKGROUP, task.getWorkgroup())
			.where(TASK.ID.eq(task.getId())).execute();
	}
	
	public static TaskComment getTaskComment(AONContext ctx, Integer taskCommentId) {
		return ctx.getDslContext().select()
			.from(TASK_COMMENT)
			.where(TASK_COMMENT.ID.eq(taskCommentId))
			.fetchInto(TASK_COMMENT).stream().map(new FullTaskCommentFiller()).findFirst().orElse(new TaskComment());
	}
	
	public static TaskComment getLastTaskComment(AONContext ctx, Integer taskId) {
		return ctx.getDslContext().select()
				.from(TASK_COMMENT)
				.where(TASK_COMMENT.TASK.eq(taskId)).orderBy(TASK_COMMENT.ID.desc()).limit(1)
				.fetchInto(TASK_COMMENT).stream().map(new FullTaskCommentFiller()).findFirst().orElse(new TaskComment());
	}

	public static TaskComment createTaskComment(AONContext ctx, TaskComment taskComment, Integer taskId) {
		Integer id = ctx.getDslContext().insertInto(TASK_COMMENT, TASK_COMMENT.COMMENT, TASK_COMMENT.CREATION_DATE, TASK_COMMENT.DOMAIN, TASK_COMMENT.TASK, TASK_COMMENT.MODIFICATION_DATE,
				TASK_COMMENT.CREATION_USER, TASK_COMMENT.MODIFICATION_USER)
			.values(taskComment.getComment(), taskComment.toTimestamp(taskComment.getCreationDate()), taskComment.getDomain(), taskComment.getTask(), taskComment.toTimestamp(taskComment.getModificationDate()),
					taskComment.getCreationUser(), taskComment.getModificationUser())
			.returning(TASK_COMMENT.ID).fetchOne().getId();
		return taskComment.setId(id);	
	}

	public static TaskComment updateTaskComment(AONContext ctx, TaskComment taskComment) {
		return ctx.getDslContext().update(TASK_COMMENT)
			.set(TASK_COMMENT.COMMENT, taskComment.getComment())
			.set(TASK_COMMENT.MODIFICATION_USER, taskComment.getModificationUser())
			.set(TASK_COMMENT.MODIFICATION_DATE, taskComment.toTimestamp(taskComment.getModificationDate()))
		.where(TASK_COMMENT.ID.eq(taskComment.getId()))
		.returning().fetch().stream().map(new FullTaskCommentFiller()).findFirst().orElse(new TaskComment());
	}

	public static TaskEvent getTaskEvent(AONContext ctx, Integer taskEventId) {
		return ctx.getDslContext().select()
				.from(TASK_EVENT)
				.where(TASK_EVENT.ID.eq(taskEventId))
				.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller()).findFirst().orElse(new TaskEvent());
	}
	
	public static TaskEvent getLastTaskEvent(AONContext ctx, Integer taskId) {
		return ctx.getDslContext().select()
				.from(TASK_EVENT)
				.where(TASK_EVENT.TASK.eq(taskId)).orderBy(TASK_EVENT.ID.desc()).limit(1)
				.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller()).findFirst().orElse(new TaskEvent());
	}
	
	public static TaskEvent getTaskEvent(AONContext ctx, TaskEventFilter filter) {
		return ctx.getDslContext().select()
				.from(TASK_EVENT)
				.where(TASK_EVENT_PROPERTIES.getConditions(filter))
				.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller()).findFirst().orElse(new TaskEvent());
	}

	public static TaskEvent createTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskId) {
		Integer id = ctx.getDslContext().insertInto(TASK_EVENT, TASK_EVENT.EVENT, TASK_EVENT.CREATION_DATE, TASK_EVENT.DOMAIN, TASK_EVENT.TASK,
				TASK_EVENT.CREATION_USER, TASK_EVENT.MODIFICATION_USER, TASK_EVENT.MODIFICATION_DATE)
				.values(taskEvent.getEvent(), taskEvent.toTimestamp(taskEvent.getCreationDate()), taskEvent.getDomain(), taskEvent.getTask(),
						taskEvent.getCreationUser(), taskEvent.getModificationUser(), taskEvent.toTimestamp(taskEvent.getModificationDate()))
				.returning(TASK_EVENT.ID).fetchOne().getId();
		return taskEvent.setId(id);
	}

	public static TaskEvent updateTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskEventId) {
		ctx.getDslContext().update(TASK_EVENT)
			.set(TASK_EVENT.EVENT, taskEvent.getEvent())
			.set(TASK_EVENT.MODIFICATION_USER, taskEvent.getModificationUser())
			.set(TASK_EVENT.MODIFICATION_DATE, taskEvent.toTimestamp(taskEvent.getModificationDate()))
			.where(TASK_EVENT.ID.eq(taskEventId))
		.execute();
		return taskEvent;
	}
	
	public static Stream<Registry> getTaskMemberStream(AONContext ctx, String filter){
		return ctx.getDslContext().select().from(REGISTRY).join(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(REGISTRY.ID))
			.where(REGISTRY.DOMAIN.eq(ctx.getDomainId())).and(REGISTRY.NAME.like(filter))
			.fetchInto(REGISTRY).stream().map(new TaskRegistryFiller());
	}
	
	public static Stream<Registry> getTaskRegistryStream(AONContext ctx){
		return ctx.getDslContext().select().from(REGISTRY).join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
			.where(REGISTRY.DOMAIN.eq(ctx.getDomainId())).and(CUSTOMER.STATUS.eq((byte) 0))
			.fetchInto(REGISTRY).stream().map(new TaskRegistryFiller());
	}
	
	public static Stream<Registry> getFilterRegistryStream(AONContext ctx, String filter){
		return ctx.getDslContext().select()
				.from(REGISTRY).join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
							.join(RMEDIA).on(RMEDIA.REGISTRY.eq(REGISTRY.ID))
			.where(REGISTRY.DOMAIN.eq(ctx.getDomainId())).and(CUSTOMER.STATUS.eq((byte) 0))
				.and(REGISTRY.NAME.contains(filter).or(REGISTRY.ALIAS.contains(filter)).or(RMEDIA.VALUE.eq(filter)).or(REGISTRY.DOCUMENT.contains(filter)))
			.fetchInto(REGISTRY).stream().map(new TaskRegistryFiller());
	}
	
	public static Stream<Workgroup> getTaskWorkgroupStream(AONContext ctx, String filter){
		return ctx.getDslContext().select().from(WORKGROUP).where(WORKGROUP.DOMAIN.eq(ctx.getDomainId()))
			.and(WORKGROUP.DESCRIPTION.like(filter)).fetchInto(WORKGROUP).stream().map(new FullWorkgroupFiller());
	}
	
	public static void deleteTaskTag(AONContext ctx, Integer id, TagType tagType){
		LinkedList<Integer> list = TagDAO.getTagStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getTypeProperty().eq(tagType.value())))
			.map(a -> a.getId()).collect(Collectors.toCollection(LinkedList::new));
		ctx.getDslContext().delete(TASK_TAG).where(TASK_TAG.TASK.eq(id)).and(TASK_TAG.TAG.in(list)).execute();
	}
	
	public static void deleteTaskTag(AONContext ctx, TaskTagFilter filter){
		ctx.getDslContext().delete(TASK_TAG).where(TASK_TAG_PROPERTIES.getConditions(filter)).execute();
	}
	
	public static void createTaskTag(AONContext ctx, TaskTag taskTag ){
		ctx.getDslContext().insertInto(TASK_TAG, TASK_TAG.DOMAIN, TASK_TAG.TAG, TASK_TAG.TASK)
		.values(taskTag.getDomain(), taskTag.getTag(), taskTag.getTask()).execute();
	}
	
	public static Workgroup getWorkgroup(AONContext ctx, Integer wId ){
		return ctx.getDslContext().select().from(WORKGROUP).where(WORKGROUP.ID.eq(wId))
		.fetchInto(WORKGROUP).stream().map(new FullWorkgroupFiller()).findFirst().orElse(new Workgroup());		
	}
	
	private static class FullTaskFiller implements Function<TaskRecord, Task> {
		@Override
		public Task apply(TaskRecord r) {
			return new Task().setId(r.getId())
					.setDomain(r.getDomain())
					.setActivityType(r.getActivityType())
					.setComments(r.getComments())
					.setDescription(r.getDescription())
					.setDueDate(r.getDueDate())
					.setEndDate(r.getEndDate())
					.setGtaskId(r.getGtaskId())
					.setGtasklistId(r.getGtasklistId())
					.setPercent(r.getPercent())
					.setPriority(r.getPriority())
					.setProject(r.getProject())
					.setRegistry(r.getRegistry())
					.setRepeatPeriod(r.getRepeatPeriod())
					.setSender(r.getSender())
					.setSource(r.getSource())
					.setStartDate(r.getStartDate())
					.setStatus(r.getStatus())
					.setTaskHolder(r.getTaskHolder())
					.setWorkgroup(r.getWorkgroup())
					.setNumber(r.getNumber())
					.setCreationUser(r.getCreationUser())
					.setCreationDate(r.getCreationDate())
					.setModificationUser(r.getModificationUser())
					.setModificationDate(r.getModificationDate());
		}
	}
	
	private static class TaskRegistryFiller implements Function<RegistryRecord, Registry> {
		@Override
		public Registry apply(RegistryRecord r) {
			return new Registry().setId(r.getId())
					.setDomain(r.getDomain())
					.setAlias(r.getAlias())
					.setName(r.getName())
					.setType(r.getType());
		}
	}
	
	private static class FullWorkgroupFiller implements Function<WorkgroupRecord, Workgroup> {
		@Override
		public Workgroup apply(WorkgroupRecord r) {
			return new Workgroup().setId(r.getId())
					.setDomain(r.getDomain())
					.setDescription(r.getDescription())
					.setStatus(r.getStatus());
		}
	}
	
	private static class FullTagFiller implements Function<TagRecord, Tag> {
		@Override
		public Tag apply(TagRecord r) {
			return new Tag().setId(r.getId())
					.setColor(r.getColor())
					.setDomain(r.getDomain())
					.setName(r.getName())
					.setType(r.getType());		
		}
	}
	
	private static class FullTaskCommentFiller implements Function<TaskCommentRecord, TaskComment> {
		@Override
		public TaskComment apply(TaskCommentRecord r) {
			return new TaskComment().setId(r.getId())
					.setDomain(r.getDomain())
					.setComment(r.getComment())
					.setTask(r.getTask())
					.setCreationUser(r.getCreationUser())
					.setCreationDate(r.getCreationDate())
					.setModificationUser(r.getModificationUser())
					.setModificationDate(r.getModificationDate());		
		}
	}
	
	private static class FullTaskEventFiller implements Function<TaskEventRecord, TaskEvent> {
		@Override
		public TaskEvent apply(TaskEventRecord r) {
			return new TaskEvent().setId(r.getId())
					.setDomain(r.getDomain())
					.setEvent(r.getEvent())
					.setTask(r.getTask())
					.setCreationUser(r.getCreationUser())
					.setCreationDate(r.getCreationDate())
					.setModificationUser(r.getModificationUser())
					.setModificationDate(r.getModificationDate());
		}
	}
}




