package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;
import static com.esferalia.aon.jooq.tables.TaskEvent.TASK_EVENT;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.sql.Date;
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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
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
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskDAO {
	
	private static final TaskPropertiesDAO TASK_PROPERTIES = new TaskPropertiesDAO();
	private static final TaskTagPropertiesDAO TASK_TAG_PROPERTIES = new TaskTagPropertiesDAO();

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
		@Override public Property<Date> getDueDateProperty() {return new FilterDAO.PropertyDAO<Date>(TASK.DUE_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<Date>(TASK.END_DATE);}
		@Override public Property<String> getGtaskIdProperty() {return new FilterDAO.PropertyDAO<String>(TASK.GTASK_ID);}
		@Override public Property<String> getGtasklisIdProperty() {return new FilterDAO.PropertyDAO<String>(TASK.GTASKLIST_ID);}
		@Override public Property<Byte> getPercentProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.PERCENT);}
		@Override public Property<Byte> getPriorityProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.PRIORITY);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.PROJECT);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.REGISTRY);}
		@Override public Property<Byte> getRepeatPeriodProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.REPEAT_PERIOD);}
		@Override public Property<Integer> getSenderProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK.SENDER);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<Byte>(TASK.SOURCE);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<Date>(TASK.START_DATE);}
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
			.fetchInto(TASK_COMMENT).stream().map(new FullTaskCommentFiller(ctx));
	}
	
	public static Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId){
		return ctx.getDslContext()
			.select()
			.from(TASK_EVENT)
			.where(TASK_EVENT.TASK.eq(taskId))
			.orderBy(TASK_EVENT.ID.desc())
			.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller(ctx));
	}

	public static Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter){
		return ctx.getDslContext().select().from(TAG).join(TASK_TAG).on(TAG.ID.eq(TASK_TAG.TAG))
			.where(TASK_TAG_PROPERTIES.getConditions(filter)).fetchInto(TAG).stream().map(new FullTagFiller());
	}
	
	public static Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter){
		// state 
		Condition c;
		if(issueFilter.getState().equals("open")) c = TASK.STATUS.eq(TaskStatus.OPEN.value());
		else if(issueFilter.getState().equals("closed")) c = TASK.STATUS.eq(TaskStatus.CLOSED.value());
		else c = TASK.STATUS.eq(TaskStatus.OPEN.value()).or(TASK.STATUS.eq(TaskStatus.CLOSED.value()));
		
		// assignee
		if(issueFilter.getAssignee() != null && !issueFilter.getAssignee().equals(""))
			c = c.and(TASK.REGISTRY.eq(1)); // TODO 

		// creator
		if(issueFilter.getCreator() != null && !issueFilter.getCreator().equals(""))
			c = c.and(TASK.TASK_HOLDER.eq(1)); // TODO
		
		// labels
		if(issueFilter.getLabels() != null && !issueFilter.getLabels().equals("")){
			String[] labels = issueFilter.getLabels().split(",");
			for (String label : labels){
				//c = c.and(TAG.NAME.eq(label));
			}
		}
		
		// mentioned
		if(issueFilter.getMentioned() != null && !issueFilter.getMentioned().equals("")){}
		
		// milestone
		if(issueFilter.getMilestone() != null && !issueFilter.getMilestone().equals("")){}
		
		// since
		if(issueFilter.getSince() != null && !issueFilter.getSince().equals("")){}
		
		// sort    created | updated | comments
		TableField<TaskRecord, Date> sort = TASK.START_DATE; 
 		if(issueFilter.getSort() != null && !issueFilter.getSort().equals("updated")){
 			//**** sort = TASK.UPDATE_DATE;
		}
 		
		// direction
 		SortField<Date> sortDir = sort.desc();
		if(issueFilter.getDirection() != null && !issueFilter.getDirection().equals("asc")){
			sortDir = sort.asc();
		}
		
		// title
		if(issueFilter.getTitle() != null && !issueFilter.getTitle().equals(""))
			c = c.and(TASK.DESCRIPTION.contains(issueFilter.getTitle()));			
		
		return ctx.getDslContext().select().from(TASK) 
				.where(TASK_PROPERTIES.getConditions(filter)).and(c).and(TASK.NUMBER.isNotNull()).orderBy(sortDir)
				.limit(issueFilter.getPerPage())
				.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
				.fetchInto(TASK).stream().map(new FullTaskFiller());
	}

	public static Integer createTask(AONContext ctx, Task task) {
		return ctx.getDslContext().insertInto(TASK, TASK.ACTIVITY_TYPE, TASK.COMMENTS, TASK.DESCRIPTION, TASK.DOMAIN, TASK.DUE_DATE, TASK.END_DATE, TASK.GTASK_ID, TASK.GTASKLIST_ID,TASK.NUMBER, TASK.USER,
				TASK.PERCENT, TASK.PRIORITY, TASK.PROJECT, TASK.REGISTRY, TASK.REPEAT_PERIOD, TASK.SENDER, TASK.SOURCE, TASK.START_DATE, TASK.STATUS, TASK.TASK_HOLDER, TASK.UPDATE_DATE, TASK.WORKGROUP)
			.values(task.getActivityType(),task.getComments(), task.getDescription(), task.getDomain(), AonDateUtils.toSql(task.getDueDate()), AonDateUtils.toSql(task.getEndDate()), task.getGtaskId(), task.getGtasklistId(), task.getNumber(), task.getUser(),
				task.getPercent(), task.getPriority(), task.getProject(), task.getRegistry(), task.getRepeatPeriod(), task.getSender(), task.getSource(), AonDateUtils.toSql(task.getStartDate()), task.getStatus(), task.getTaskHolder(), AonDateUtils.toSql(task.getUpdateDate()), task.getWorkgroup())
			.returning(TASK.ID).fetchOne().getId();
	}
	
	public static void updateTaskStatus(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)
			.set(TASK.STATUS, task.getStatus())
			.set(TASK.END_DATE, AonDateUtils.toSql(task.getEndDate()))
			.where(TASK.ID.eq(task.getId())).execute();
	}
	
	public static void updateTaskUser(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)			
			.set(TASK.TASK_HOLDER, task.getTaskHolder())
			.set(TASK.USER,  task.getUser())
			.set(TASK.WORKGROUP, task.getWorkgroup())
			.where(TASK.ID.eq(task.getId())).execute();
	}
	
	public static TaskComment getTaskComment(AONContext ctx, Integer taskCommentId) {
		return ctx.getDslContext().select()
			.from(TASK_COMMENT)
			.where(TASK_COMMENT.ID.eq(taskCommentId))
			.fetchInto(TASK_COMMENT).stream().map(new FullTaskCommentFiller(ctx)).findFirst().orElse(new TaskComment());
	}

	public static TaskComment createTaskComment(AONContext ctx, TaskComment taskComment, Integer taskId) {
		Integer id = ctx.getDslContext().insertInto(TASK_COMMENT, TASK_COMMENT.COMMENT, TASK_COMMENT.CREATE_DATE, TASK_COMMENT.DOMAIN, TASK_COMMENT.USER, TASK_COMMENT.TASK, TASK_COMMENT.UPDATE_DATE)
			.values(taskComment.getComment(), AonDateUtils.toSql(taskComment.getCreateDate()), taskComment.getDomain(), taskComment.getUser().getId(), taskComment.getTask(), AonDateUtils.toSql(taskComment.getUpdateDate()))
			.returning(TASK_COMMENT.ID).fetchOne().getId();
		return taskComment.setId(id);	
	}

	public static TaskComment updateTaskComment(AONContext ctx, TaskComment taskComment, Integer taskCommentId) {
		ctx.getDslContext().update(TASK_COMMENT)
			.set(TASK_COMMENT.COMMENT, taskComment.getComment())
			.set(TASK_COMMENT.UPDATE_DATE, AonDateUtils.toSql(taskComment.getUpdateDate()))
		.where(TASK_COMMENT.ID.eq(taskCommentId))
		.execute();
		return taskComment;
	}

	public static TaskEvent getTaskEvent(AONContext ctx, Integer taskEventId) {
		return ctx.getDslContext().select()
				.from(TASK_EVENT)
				.where(TASK_EVENT.ID.eq(taskEventId))
				.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller(ctx)).findFirst().orElse(new TaskEvent());
	}

	public static TaskEvent createTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskId) {
		Integer id = ctx.getDslContext().insertInto(TASK_EVENT, TASK_EVENT.EVENT, TASK_EVENT.CREATE_DATE, TASK_EVENT.DOMAIN, TASK_EVENT.USER, TASK_EVENT.TASK)
				.values(taskEvent.getEvent(), AonDateUtils.toSql(taskEvent.getCreateDate()), taskEvent.getDomain(), taskEvent.getUser().getId(), taskEvent.getTask())
				.returning(TASK_EVENT.ID).fetchOne().getId();
		return taskEvent.setId(id);
	}

	public static TaskEvent updateTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskEventId) {
		ctx.getDslContext().update(TASK_EVENT)
			.set(TASK_EVENT.EVENT, taskEvent.getEvent())
			.where(TASK_EVENT.ID.eq(taskEventId))
		.execute();
		return taskEvent;
	}
	
	public static Stream<Registry> getTaskMemberStream(AONContext ctx, String filter){
		return ctx.getDslContext().select().from(REGISTRY).join(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(REGISTRY.ID))
			.where(REGISTRY.DOMAIN.eq(ctx.getDomainId())).and(REGISTRY.NAME.like(filter))
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
					.setUpdateDate(r.getUpdateDate())
					.setUser(r.getUser());
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
		AONContext ctx;
		public FullTaskCommentFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public TaskComment apply(TaskCommentRecord r) {
			return new TaskComment().setId(r.getId())
					.setDomain(r.getDomain())
					.setComment(r.getComment())
					.setCreateDate(r.getCreateDate())
					.setUser(AON.getUser(ctx.getDomainId(), ctx.getDomainName(), ctx.getUser(), r.getUser()))
					.setTask(r.getTask())
					.setUpdateDate(r.getUpdateDate());		
		}
	}
	
	private static class FullTaskEventFiller implements Function<TaskEventRecord, TaskEvent> {
		AONContext ctx;
		public FullTaskEventFiller(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public TaskEvent apply(TaskEventRecord r) {
			return new TaskEvent().setId(r.getId())
					.setDomain(r.getDomain())
					.setEvent(r.getEvent())
					.setCreateDate(r.getCreateDate())
					.setUser(AON.getUser(ctx.getDomainId(), ctx.getDomainName(), ctx.getUser(), r.getUser()))
					.setTask(r.getTask());
		}
	}
}




