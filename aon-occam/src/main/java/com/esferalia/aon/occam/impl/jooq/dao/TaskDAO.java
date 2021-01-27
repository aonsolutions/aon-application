package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Tag.TAG;
import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.TaskComment.TASK_COMMENT;
import static com.esferalia.aon.jooq.tables.TaskEvent.TASK_EVENT;
import static com.esferalia.aon.jooq.tables.TaskHolder.TASK_HOLDER;
import static com.esferalia.aon.jooq.tables.TaskHolderWorkgroup.TASK_HOLDER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.TaskTag.TASK_TAG;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.SortField;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.records.TagRecord;
import com.esferalia.aon.jooq.tables.records.TaskCommentRecord;
import com.esferalia.aon.jooq.tables.records.TaskEventRecord;
import com.esferalia.aon.jooq.tables.records.TaskHolderRecord;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.jooq.tables.records.WorkgroupRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.TaskCommentFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskEventFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderWorkgroupFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
import com.esferalia.aon.occam.api.model.Properties.TaskCommentProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskEventProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderWorkgroupProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskTagProperties;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.FillerDAO.DomainFiller;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TaskDAO {
	
	private static final TaskPropertiesDAO TASK_PROPERTIES = new TaskPropertiesDAO();
	private static final TaskTagPropertiesDAO TASK_TAG_PROPERTIES = new TaskTagPropertiesDAO();
	private static final TaskEventPropertiesDAO TASK_EVENT_PROPERTIES = new TaskEventPropertiesDAO();
	private static final TaskCommentPropertiesDAO TASK_COMMENT_PROPERTIES = new TaskCommentPropertiesDAO();
	private static final TaskHolderWorkgroupPropertiesDAO TASK_HOLDER_WORKGROUP_PROPERTIES = new TaskHolderWorkgroupPropertiesDAO();
	private static final TaskHolderPropertiesDAO TASK_HOLDER_PROPERTIES = new TaskHolderPropertiesDAO();
	
	protected static class TaskHolderWorkgroupPropertiesDAO implements TaskHolderWorkgroupProperties {
		protected Condition[] getConditions(TaskHolderWorkgroupFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_HOLDER_WORKGROUP.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_HOLDER_WORKGROUP.DOMAIN);}
		@Override public Property<Integer> getTaskHolderProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_HOLDER_WORKGROUP.TASK_HOLDER);}
		@Override public Property<Integer> getWorkgroupProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_HOLDER_WORKGROUP.WORKGROUP);}
	}
	
	protected static class TaskHolderPropertiesDAO implements TaskHolderProperties {
		protected Condition[] getConditions(TaskHolderFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.TYPE);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.ACTIVE);}
		@Override public Property<Integer> getUserIdProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.USER_ID);}
		@Override public Property<Integer> getCostProfileProperty() {return new FilterDAO.PropertyDAO<>(TASK_HOLDER.COST_PROFILE);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
	}
	
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
	
	protected static class TaskCommentPropertiesDAO implements TaskCommentProperties {
		protected Condition[] getConditions(TaskCommentFilter filter) {	
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_COMMENT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_COMMENT.DOMAIN);}
		@Override public Property<Integer> getTaskProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_COMMENT.TASK);}
		@Override public Property<String> getCommentProperty() {return new FilterDAO.PropertyDAO<String>(TASK_COMMENT.COMMENT);}
		@Override public Property<Integer> getSourceProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_COMMENT.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<Integer>(TASK_COMMENT.SOURCE_ID);}		
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK_COMMENT.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK_COMMENT.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(TASK_COMMENT.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<Timestamp>(TASK_COMMENT.MODIFICATION_DATE);}
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
	
	public static void deleteTask(AONContext ctx, TaskFilter filter){
		ctx.getDslContext().delete(TASK)
		.where(TASK_PROPERTIES.getConditions(filter))
		.execute();
	}
	
	public static Integer getCommentsCount(AONContext ctx, Integer taskId){
		return ctx.getDslContext()
			.select(DSL.count(TASK_COMMENT.ID))
			.from(TASK_COMMENT)
			.where(TASK_COMMENT.TASK.eq(taskId))
			.fetchOne().value1();
	}
	
	public static Stream<TaskComment> getTaskCommentStream(AONContext ctx, TaskCommentFilter filter){
		return ctx.getDslContext()
			.select()
			.from(TASK_COMMENT)
			.where(TASK_COMMENT_PROPERTIES.getConditions(filter))
			.orderBy(TASK_COMMENT.CREATION_DATE.asc())
			.fetchInto(TASK_COMMENT).stream().map(new FullTaskCommentFiller());
	}
	
	public static void deleteTaskComment(AONContext ctx, TaskCommentFilter filter){
		ctx.getDslContext().delete(TASK_COMMENT)
			.where(TASK_COMMENT_PROPERTIES.getConditions(filter))
			.execute();				
	}
	public static Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId){
		return ctx.getDslContext()
			.select()
			.from(TASK_EVENT)
			.where(TASK_EVENT.TASK.eq(taskId))
			.orderBy(TASK_EVENT.ID.desc())
			.fetchInto(TASK_EVENT).stream().map(new FullTaskEventFiller());
	}

	public static void deleteTaskEvent(AONContext ctx, TaskEventFilter filter){
		ctx.getDslContext().delete(TASK_EVENT)
			.where(TASK_EVENT_PROPERTIES.getConditions(filter))
			.execute();				
	}
	
	public static Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter){
		return ctx.getDslContext().select().from(TAG).join(TASK_TAG).on(TAG.ID.eq(TASK_TAG.TAG))
			.where(TASK_TAG_PROPERTIES.getConditions(filter)).fetchInto(TAG).stream().map(new FullTagFiller());
	}
	
	
	private static Condition getIssueFilterCondition(AONContext ctx, IssueFilter issueFilter) {
		Condition c;
		//state
		if(issueFilter.getState().equals("open")) c = TASK.STATUS.eq(TaskStatus.PENDING.value())
				.or(TASK.STATUS.eq(TaskStatus.IN_PROGRESS.value()));
		else if(issueFilter.getState().equals("closed")) 
			c = TASK.STATUS.eq(TaskStatus.FINISHED.value())
				.or(TASK.STATUS.eq(TaskStatus.FAQ.value()).and(TASK.PARENT.isNotNull()));
		else if(issueFilter.getState().equals("deleted")) c = TASK.STATUS.eq(TaskStatus.DELETED.value());
		else if(issueFilter.getState().equals("faq")) c = TASK.STATUS.eq(TaskStatus.FAQ.value()).and(TASK.PARENT.isNull());
		else c = TASK.STATUS.ne(TaskStatus.DELETED.value()).and(TASK.STATUS.ne(TaskStatus.FAQ.value()));

		// mine (for fast filter)
		if(issueFilter.getMine() != null && !issueFilter.getMine().equals("")){
			User user = SecurityDAO.getUser(ctx, issueFilter.getMine());
			TaskHolder taskHolder = getTaskHolder(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getUserIdProperty().eq(user.getId())));
			LinkedList<Integer> workgroups = getTaskHolderWorkgroupStream(ctx, f -> f.getTaskHolderProperty().eq(taskHolder.getId())).
					map(r -> r.getId()).collect(Collectors.toCollection(LinkedList::new));
			Condition m = TASK.CREATION_USER.eq(issueFilter.getMine())
				.or(TASK.MODIFICATION_USER.eq(issueFilter.getMine()))
				.or(TASK_COMMENT.CREATION_USER.eq(issueFilter.getMine()))
				.or(TASK_EVENT.CREATION_USER.eq(issueFilter.getMine()))
				.or(TASK_EVENT.MODIFICATION_USER.eq(issueFilter.getMine()));
			if(taskHolder.getId() != null) m = m.or(TASK.TASK_HOLDER.eq(taskHolder.getId()));
			if(issueFilter.getAssignee() != null && !issueFilter.getAssignee().isEmpty() && issueFilter.getAssignee().contains(-1)){
				m = m.or(TASK.TASK_HOLDER.isNull());
				issueFilter.setAssignee(null);
			}
			if(!workgroups.isEmpty()) m = m.or(TASK.WORKGROUP.in(workgroups));
			if(issueFilter.getWorkgroup() != null  && !issueFilter.getWorkgroup().isEmpty() && issueFilter.getWorkgroup().contains(-1)){
				m = m.or(TASK.WORKGROUP.isNull());
				issueFilter.setWorkgroup(null);
			}
			c = c.and(m);
		}
		
		// assignee
		if(issueFilter.getAssignee() != null && !issueFilter.getAssignee().isEmpty()){
			Condition m = TASK.TASK_HOLDER.in(issueFilter.getAssignee());
			if(issueFilter.getAssignee().contains(-1)) m = m.or(TASK.TASK_HOLDER.isNull());
			if(issueFilter.getWorkgroup() != null && (issueFilter.getWorkgroup().isEmpty()
				|| (issueFilter.getWorkgroup().size() == 1 && issueFilter.getWorkgroup().get(0) == -1))){
				LinkedList<Integer> L =getTaskHolderWorkgroupStream(ctx,
						f -> f.getTaskHolderProperty().in(issueFilter.getAssignee().toArray(new Integer[issueFilter.getAssignee().size()])))
					.map(r -> r.getId()).collect(Collectors.toCollection(LinkedList::new));
				m = m.or(TASK.WORKGROUP.in(L).and(TASK.TASK_HOLDER.isNull()));
				issueFilter.setWorkgroup(null);
			}
			c = c.and(m);			
		}
		
		// WORKGROUP
		if(issueFilter.getWorkgroup() != null && !issueFilter.getWorkgroup().isEmpty()){
			Condition m = TASK.WORKGROUP.in(issueFilter.getWorkgroup());
			if(issueFilter.getWorkgroup().contains(-1)) m = m.or(TASK.WORKGROUP.isNull());
			c = c.and(m);
		}

		// enterprise
		if(issueFilter.getEnterprise() != null && !issueFilter.getEnterprise().equals(""))
			c = c.and(TASK.REGISTRY.eq(Integer.parseInt(issueFilter.getEnterprise())));

		// creator
		if(issueFilter.getCreator() != null && !issueFilter.getCreator().equals(""))
			c = c.and(TASK.CREATION_USER.eq(issueFilter.getCreator())); 
				
		// labels
		if(issueFilter.getLabels() != null && !issueFilter.getLabels().equals(""))
			c = c.and(TASK_TAG.TAG.eq(Integer.parseInt(issueFilter.getLabels())));	
				
		// mentioned
		if(issueFilter.getMentioned() != null && !issueFilter.getMentioned().equals("")){}
				
		// milestone
		if(issueFilter.getMilestone() != null && !issueFilter.getMilestone().equals("")){}
				
		// since
		if(issueFilter.getSince() != null && !issueFilter.getSince().equals("")){}
				
		// title
		if(issueFilter.getTitle() != null && !issueFilter.getTitle().equals(""))
			if(AonStringUtils.isNumeric(issueFilter.getTitle()))
				c = c.and(TASK.DESCRIPTION.contains(issueFilter.getTitle())
					.or(TASK.COMMENTS.contains(issueFilter.getTitle()))
					.or(TASK.NUMBER.eq(Integer.parseInt(issueFilter.getTitle())))
					.or(TASK_COMMENT.COMMENT.contains(issueFilter.getTitle())));	
			else c = c.and(TASK.DESCRIPTION.contains(issueFilter.getTitle())
					.or(TASK.COMMENTS.contains(issueFilter.getTitle()))
					.or(TASK_COMMENT.COMMENT.contains(issueFilter.getTitle())));
		// type
		if(issueFilter.getType() != null && !issueFilter.getType().equals("")){
			if(!AonStringUtils.isNumeric(issueFilter.getType())){
				Tag tag = TagDAO.getTagStream(ctx, f -> f.getDomainProperty().eq(ctx.getDomainId()).and(f.getNameProperty().eq(issueFilter.getType()))
					.and(f.getTypeProperty().eq(TagType.TASK_TYPE.value()))).findFirst().orElse(new Tag());
				c = c.and(TASK_TAG.TAG.eq(tag.getId()));
			} else c = c.and(TASK_TAG.TAG.eq(Integer.parseInt(issueFilter.getType())));
		}
				
		// priority
		if(issueFilter.getPriority() != null && !issueFilter.getPriority().equals(""))
			c = c.and(TASK.PRIORITY.eq(Priority.valueNameOf(issueFilter.getPriority()).value()));

		// date_diff
		if(issueFilter.getDateDiff() != null && !issueFilter.getDateDiff().equals("")){
			Calendar cal = Calendar.getInstance();
			System.out.println(new Timestamp(cal.getTimeInMillis()));
			cal.add(Calendar.DAY_OF_WEEK, - (Integer.parseInt(issueFilter.getDateDiff())));
			System.out.println(new Timestamp(cal.getTimeInMillis()));
			Timestamp as = new Timestamp(cal.getTimeInMillis());
			c = c.and(TASK.START_DATE.greaterOrEqual(as));
		}
		return c;
	}
	
	private static SortField<Timestamp> getIssueFilterSortField(IssueFilter issueFilter) {
		// sort    created | updated | comments
		TableField<TaskRecord, Timestamp> sort = TASK.START_DATE; 
		if(issueFilter.getSort() != null && issueFilter.getSort().equals("updated"))
			sort = TASK.MODIFICATION_DATE;
		 		
		// direction
		SortField<Timestamp> sortDir = sort.desc();
		if(issueFilter.getDirection() != null && issueFilter.getDirection().equals("asc"))
			sortDir = sort.asc();
		return sortDir;
	}
	
	//[open, close, delete]
	public static Integer[] getTaskCount(AONContext ctx, TaskFilter filter, IssueFilter issueFilter){
		Boolean tagBool = (issueFilter.getLabels() != null && !issueFilter.getLabels().equals(""))
				|| (issueFilter.getType() != null && !issueFilter.getType().equals(""));
		Boolean commentBool = issueFilter.getTitle() != null && !issueFilter.getTitle().equals("");
		Boolean eventBool = false;
		if(issueFilter.getMine() != null && !issueFilter.getMine().equals("")){
			tagBool = true;
			commentBool = true;
			eventBool = true;
		}
		Condition openCondition = getIssueFilterCondition(ctx, issueFilter.setState("open")); 
		Condition closedCondition = getIssueFilterCondition(ctx, issueFilter.setState("closed")); 
		Condition deletedCondition = getIssueFilterCondition(ctx, issueFilter.setState("deleted")); 
		Condition extra = TASK.PARENT.isNull().or(TASK.PARENT.eq(TASK.ID))
					.or(TASK.PARENT.isNotNull().and(TASK.STATUS.eq(TaskStatus.FAQ.value())));
		
		if(tagBool && commentBool && eventBool){
			Integer open = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_EVENT).on(TASK_EVENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(openCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer close = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_EVENT).on(TASK_EVENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(closedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer delete = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_EVENT).on(TASK_EVENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(deletedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			return new Integer[]{open,close,delete};
		} else if(tagBool && commentBool){
			Integer open = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(openCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer close = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(closedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer delete = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(deletedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			return new Integer[]{open,close,delete};
		} else if(tagBool){
			Integer open = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(openCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer close = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(closedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer delete = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(deletedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			return new Integer[]{open,close,delete};
		} else if(commentBool){
			Integer open = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(openCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer close = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(closedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			
			Integer delete = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
						.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(deletedCondition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.fetch().size();
			return new Integer[]{open,close,delete};
		} 
		
		Integer open = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
				.where(TASK_PROPERTIES.getConditions(filter)).and(openCondition).and(TASK.NUMBER.isNotNull())
				.and(extra)
				.fetch().size();
		
		Integer close = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
				.where(TASK_PROPERTIES.getConditions(filter)).and(closedCondition).and(TASK.NUMBER.isNotNull())
				.and(extra)
				.fetch().size();
		
		Integer delete = ctx.getDslContext().selectDistinct(TASK.ID).from(TASK)
				.where(TASK_PROPERTIES.getConditions(filter)).and(deletedCondition).and(TASK.NUMBER.isNotNull())
				.and(extra)
				.fetch().size();
		return new Integer[]{open,close,delete};
	}
	
	public static Stream<Task> getDuplicateTaskStream(AONContext ctx, Integer parent){
		return ctx.getDslContext().select().from(TASK).where(TASK.PARENT.eq(parent))
			.orderBy(TASK.NUMBER.desc()).fetchInto(TASK).stream().map(new FullTaskFiller());
	}
	
	public static Boolean isTaskParent(AONContext ctx, Integer parentId){
		return ctx.getDslContext().selectCount().from(TASK).where(TASK.PARENT.eq(parentId)).fetchOne(0, int.class) > 0;
	}
	
	public static Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter){
		return ctx.getDslContext().selectDistinct().from(TASK).where(TASK_PROPERTIES.getConditions(filter))
				.orderBy(TASK.NUMBER.desc()).fetchInto(TASK).stream().map(new FullTaskFiller());
	}
	
	public static Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter){
		Boolean tagBool = (issueFilter.getLabels() != null && !issueFilter.getLabels().equals(""))
				|| (issueFilter.getType() != null && !issueFilter.getType().equals(""));
		Boolean commentBool = issueFilter.getTitle() != null && !issueFilter.getTitle().equals("");
		Boolean eventBool = false;
		if(issueFilter.getMine() != null && !issueFilter.getMine().equals("")){
			tagBool = true;
			commentBool = true;
			eventBool = true;
		}
		
		Condition condition = getIssueFilterCondition(ctx, issueFilter); 
		SortField<Timestamp> sort = getIssueFilterSortField(issueFilter);
		
		Condition extra = TASK.PARENT.isNull();		 
		if(!issueFilter.getState().equals("faq")) extra = extra.or(TASK.PARENT.eq(TASK.ID))
					.or(TASK.PARENT.isNotNull().and(TASK.STATUS.eq(TaskStatus.FAQ.value())));
		if(tagBool && commentBool && eventBool){
			return ctx.getDslContext().selectDistinct(TASK.ACTIVITY_TYPE,TASK.COMMENTS,TASK.CREATION_DATE, TASK.CREATION_USER
					,TASK.DESCRIPTION,TASK.DOMAIN, TASK.DUE_DATE,TASK.END_DATE, TASK.GTASK_ID, TASK.GTASKLIST_ID, TASK.MODIFICATION_DATE, TASK.ID
					,TASK.MODIFICATION_USER, TASK.NUMBER, TASK.PARENT, TASK.PRIORITY, TASK.PERCENT, TASK.PROJECT, TASK.REGISTRY, TASK.REPEAT_PERIOD
					,TASK.SENDER, TASK.SOURCE, TASK.SOURCE_ID, TASK.START_DATE, TASK.STATUS, TASK.TASK_HOLDER, TASK.WORKGROUP)
					.from(TASK)
					.join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
					.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.leftOuterJoin(TASK_EVENT).on(TASK_EVENT.TASK.eq(TASK.ID))
				.where(TASK_PROPERTIES.getConditions(filter)).and(condition).and(TASK.NUMBER.isNotNull())
				.and(extra)
				.orderBy(sort)
				.limit(issueFilter.getPerPage())
				.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
				.fetchInto(TASK).stream().map(new FullTaskFiller());
		} else if(tagBool && commentBool){
			return ctx.getDslContext().selectDistinct().from(TASK).join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
									.leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(condition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.orderBy(sort)
					.limit(issueFilter.getPerPage())
					.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
					.fetchInto(TASK).stream().map(new FullTaskFiller());
		}else if(commentBool){
			return ctx.getDslContext().selectDistinct(
						TASK.COMMENTS, TASK.CREATION_DATE, TASK.CREATION_USER, TASK.DESCRIPTION, TASK.DOMAIN, TASK.END_DATE, TASK.WORKGROUP,TASK.ID, TASK.MODIFICATION_DATE, TASK.MODIFICATION_USER, TASK.NUMBER, TASK.PARENT, TASK.PRIORITY, TASK.PROJECT, TASK.DUE_DATE, TASK.GTASK_ID, TASK.GTASKLIST_ID, TASK.PERCENT, TASK.REGISTRY, TASK.REPEAT_PERIOD, TASK.SENDER, TASK.SOURCE, TASK.START_DATE, TASK.STATUS, TASK.TASK_HOLDER					
					).from(TASK).leftOuterJoin(TASK_COMMENT).on(TASK_COMMENT.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(condition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.orderBy(sort)
					.limit(issueFilter.getPerPage())
					.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
					.fetchInto(TASK).stream().map(new FullTaskFiller());
		} else if(tagBool){
			return ctx.getDslContext().selectDistinct().from(TASK).join(TASK_TAG).on(TASK_TAG.TASK.eq(TASK.ID))
					.where(TASK_PROPERTIES.getConditions(filter)).and(condition).and(TASK.NUMBER.isNotNull())
					.and(extra)
					.orderBy(sort)
					.limit(issueFilter.getPerPage())
					.offset(issueFilter.getPerPage() * (issueFilter.getPage() - 1))
					.fetchInto(TASK).stream().map(new FullTaskFiller());
		}else return ctx.getDslContext().selectDistinct().from(TASK) 
				.where(TASK_PROPERTIES.getConditions(filter)).and(condition).and(TASK.NUMBER.isNotNull())
				.and(extra)
				.orderBy(sort)
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
	
	public static Task updateTask(AONContext ctx, Task task) {
		ctx.getDslContext().update(TASK)
			.set(TASK.DESCRIPTION, task.getDescription())
			.set(TASK.START_DATE, task.toTimestamp(task.getStartDate()))
			.set(TASK.END_DATE,task.toTimestamp(task.getEndDate()))
			.set(TASK.DUE_DATE, task.toTimestamp(task.getDueDate()))
			.set(TASK.PRIORITY, task.getPriority())
			.set(TASK.STATUS, task.getStatus())
			.set(TASK.PERCENT, task.getPercent())
			.set(TASK.TASK_HOLDER, task.getTaskHolder())
			.set(TASK.WORKGROUP, task.getWorkgroup())
			.set(TASK.SOURCE, task.getSource())
			.set(TASK.SOURCE_ID, task.getSourceId())
			.set(TASK.PROJECT, task.getProject())
			.set(TASK.REGISTRY, task.getRegistry())
			.set(TASK.ACTIVITY_TYPE, task.getActivityType())
			.set(TASK.SENDER, task.getSender())
			.set(TASK.COMMENTS, task.getComments())
			.set(TASK.REPEAT_PERIOD, task.getRepeatPeriod())
			.set(TASK.GTASK_ID, task.getGtaskId())
			.set(TASK.GTASKLIST_ID, task.getGtasklistId())
			.set(TASK.PARENT, task.getParent())
			.set(TASK.MODIFICATION_USER, task.getModificationUser())
			.set(TASK.MODIFICATION_DATE, task.toTimestamp(task.getModificationDate()))
			.where(TASK.ID.eq(task.getId())).execute();
		return task;
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
	
	public static TaskEvent getLastTaskEvent(AONContext ctx, TaskEventFilter filter) {
		return ctx.getDslContext().select()
				.from(TASK_EVENT)
				.where(TASK_EVENT_PROPERTIES.getConditions(filter)).orderBy(TASK_EVENT.ID.desc()).limit(1)
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

	public static Stream<TaskHolder> getTaskMemberWStream(AONContext ctx, String filter, Integer workgroupId){
		return ctx.getDslContext().select().from(REGISTRY).join(TASK_HOLDER).on(TASK_HOLDER.REGISTRY.eq(REGISTRY.ID))
				.join(TASK_HOLDER_WORKGROUP).on(TASK_HOLDER.REGISTRY.eq(TASK_HOLDER_WORKGROUP.TASK_HOLDER))
				.join(DOMAIN).on(DOMAIN.ID.eq(TASK_HOLDER.DOMAIN))
			.where(REGISTRY.DOMAIN.eq(ctx.getDomainId())).and(REGISTRY.NAME.like(filter))
				.and(TASK_HOLDER_WORKGROUP.WORKGROUP.eq(workgroupId)).orderBy(REGISTRY.NAME)
			.fetch().stream().map(new TaskHolderFiller());
	}
	
	public static Stream<Customer> getFilterCustomerStream(AONContext ctx, String filter){
		return ctx.getDslContext().selectDistinct(REGISTRY.ID, REGISTRY.NAME, REGISTRY.ALIAS, CUSTOMER.STATUS)
				.from(REGISTRY).join(CUSTOMER).on(CUSTOMER.REGISTRY.eq(REGISTRY.ID))
							.leftOuterJoin(RMEDIA).on(RMEDIA.REGISTRY.eq(REGISTRY.ID))
			.where(REGISTRY.DOMAIN.eq(ctx.getDomainId()))
				.and(REGISTRY.NAME.contains(filter).or(REGISTRY.ALIAS.contains(filter)).or(RMEDIA.VALUE.contains(filter)).or(REGISTRY.DOCUMENT.contains(filter)))
			.fetch().stream().map(new TaskFilterCustomerFiller());
	}
	
	public static Stream<Workgroup> getTaskWorkgroupStream(AONContext ctx, String filter){
		return ctx.getDslContext().select().from(WORKGROUP).where(WORKGROUP.DOMAIN.eq(ctx.getDomainId()))
			.and(WORKGROUP.DESCRIPTION.like(filter)).orderBy(WORKGROUP.DESCRIPTION)
			.fetchInto(WORKGROUP).stream().map(new FullWorkgroupFiller());
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
	

	public static Workgroup insertWorkgroup(AONContext ctx, Workgroup workgroup ){
		return ctx.getDslContext().insertInto(WORKGROUP, WORKGROUP.DESCRIPTION, WORKGROUP.DOMAIN, WORKGROUP.STATUS)
				.values(workgroup.getDescription(), workgroup.getDomain(), workgroup.getStatus())
			.returning().fetch().stream().map(new FullWorkgroupFiller()).findFirst().orElse(new Workgroup());	
	}
	
	public static Workgroup updateWorkgroup(AONContext ctx, Workgroup workgroup ){
		return ctx.getDslContext().update(WORKGROUP)
				.set(WORKGROUP.DESCRIPTION, workgroup.getDescription())
				.set(WORKGROUP.STATUS, workgroup.getStatus())		
				.where(WORKGROUP.ID.eq(workgroup.getId()))
			.returning().fetch().stream().map(new FullWorkgroupFiller()).findFirst().orElse(new Workgroup());	
	}
	
	public static Workgroup deleteWorkgroup(AONContext ctx, Integer wId ){
		return ctx.getDslContext().delete(WORKGROUP)
				.where(WORKGROUP.ID.eq(wId))
			.returning().fetch().stream().map(new FullWorkgroupFiller()).findFirst().orElse(new Workgroup());	
	}

	public static Stream<TaskHolder> getTaskHolderStream(AONContext ctx, TaskHolderFilter filter){
		return ctx.getDslContext().select()
				.from(TASK_HOLDER).join(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
				.join(DOMAIN).on(DOMAIN.ID.eq(TASK_HOLDER.DOMAIN))
				.where(TASK_HOLDER_PROPERTIES.getConditions(filter))
				.fetch().stream().map(new TaskHolderFiller());
	}
	
	public static Stream<TaskHolder> getTaskHolderStream(AONContext ctx, byte[] auth){
		return ctx.getDslContext().select()
				.from(TASK_HOLDER).join(REGISTRY).on(REGISTRY.ID.eq(TASK_HOLDER.REGISTRY))
				.join(DOMAIN).on(DOMAIN.ID.eq(TASK_HOLDER.DOMAIN))
				.join(USER).on(USER.AUTH.eq(auth))
				.fetch().stream().map(new TaskHolderFiller());
	}

	
	public static TaskHolder getTaskHolder(AONContext ctx, TaskHolderFilter filter){
		return ctx.getDslContext()
				.select().from(TASK_HOLDER).where(TASK_HOLDER_PROPERTIES.getConditions(filter))
				.fetchInto(TASK_HOLDER).stream().map(new FullTaskHolderFiller()).findFirst().orElse(new TaskHolder());
	}
	
	public static TaskHolder updateTaskHolder(AONContext ctx, TaskHolder taskHolder){
		return ctx.getDslContext().update(TASK_HOLDER)
				.set(TASK_HOLDER.ACTIVE, taskHolder.getActiveValue())
				.where(TASK_HOLDER.REGISTRY.eq(taskHolder.getId()))
			.returning().fetch().stream().map(new FullTaskHolderFiller()).findFirst().orElse(new TaskHolder());	
	}
	
	public static TaskHolder insertTaskHolder(AONContext ctx, TaskHolder taskHolder){
		return ctx.getDslContext().insertInto(TASK_HOLDER, TASK_HOLDER.ACTIVE, TASK_HOLDER.COST_PROFILE, TASK_HOLDER.DOMAIN, TASK_HOLDER.REGISTRY, TASK_HOLDER.TYPE, TASK_HOLDER.USER_ID)
			.values(taskHolder.getActiveValue(), taskHolder.getCostProfile(), taskHolder.getDomain().getId(), taskHolder.getId(),
					AonEnumUtils.getByte(taskHolder.isLegalPerson()), taskHolder.getUserId())
			.returning().fetch().stream().map(new FullTaskHolderFiller()).findFirst().orElse(new TaskHolder());
	}
	
	public static TaskHolder deleteTaskHolder(AONContext ctx, Integer taskHolder){
		return ctx.getDslContext().delete(TASK_HOLDER)
				.where(TASK_HOLDER.REGISTRY.eq(taskHolder))
			.returning().fetch().stream().map(new FullTaskHolderFiller()).findFirst().orElse(new TaskHolder());
	}

	public static Stream<Workgroup> getTaskHolderWorkgroupStream(AONContext ctx, TaskHolderWorkgroupFilter filter){
		return ctx.getDslContext().select().from(TASK_HOLDER_WORKGROUP).join(WORKGROUP).on(WORKGROUP.ID.eq(TASK_HOLDER_WORKGROUP.WORKGROUP))
				.where(TASK_HOLDER_WORKGROUP_PROPERTIES.getConditions(filter))
				.fetchInto(WORKGROUP).stream().map(new FullWorkgroupFiller());
	}
	
	public static Boolean isTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter){
		return !ctx.getDslContext().select().from(TASK_HOLDER_WORKGROUP)
				.where(TASK_HOLDER_WORKGROUP_PROPERTIES.getConditions(filter))
				.fetch().isEmpty();
	}
	
	public static void insertTaskHolderWorkgroup(AONContext ctx, Integer taskHolder, Integer workgroup){
		ctx.getDslContext().insertInto(TASK_HOLDER_WORKGROUP, TASK_HOLDER_WORKGROUP.DOMAIN, TASK_HOLDER_WORKGROUP.TASK_HOLDER, TASK_HOLDER_WORKGROUP.WORKGROUP)
			.values(ctx.getDomainId(), taskHolder, workgroup).execute();
	}
	
	public static void deleteTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter){
		ctx.getDslContext().delete(TASK_HOLDER_WORKGROUP)
				.where(TASK_HOLDER_WORKGROUP_PROPERTIES.getConditions(filter))
				.execute();
	}
	
	private static class FullTaskHolderFiller implements Function<TaskHolderRecord, TaskHolder> {
		@Override
		public TaskHolder apply(TaskHolderRecord t) {
			TaskHolder taskHolder = new TaskHolder();
			taskHolder.setId(t.getRegistry());
			taskHolder.setDomain(new Domain().setId(t.getDomain()));
			return taskHolder
					.setActive(t.getActive() == (byte) 1)
					.setCostProfile(t.getCostProfile())

					.setTaskHolderType(TaskHolderType.valueOf(t.getType()))
					.setUserId(t.getUserId());
		}
	}
	
	private static class TaskHolderFiller implements Function<Record, TaskHolder> {
		@Override
		public TaskHolder apply(Record t) {
			return new TaskHolder()
					.setRegistryData( new Registry() 
						.setId(t.getValue(REGISTRY.ID))
						.setDomain(t.get(DOMAIN.ID) != null 
							? DomainFiller.buildDomain(t) 
							: new Domain().setId(t.getValue(REGISTRY.DOMAIN)))
						.setDocument(t.getValue(REGISTRY.DOCUMENT))
						.setDocumentType(DocumentType.safeValueOf(t.getValue(REGISTRY.DOCUMENT_TYPE)))
						.setDocumentCountry(Country.safeValueOf(t.getValue(REGISTRY.DOCUMENT_COUNTRY)) )
						.setName(t.getValue(REGISTRY.NAME))
						.setAlias(t.getValue(REGISTRY.ALIAS))
						.setLegalPerson(AonEnumUtils.getBoolean(t.getValue(REGISTRY.TYPE)))
						.setNationality(Country.safeValueOf(t.getValue(REGISTRY.NATIONALITY)) )
						.setSecurityLevel(SecurityLevel.safeValueOf(t.getValue(REGISTRY.SECURITY_LEVEL))))
					.setActive(t.getValue(TASK_HOLDER.ACTIVE) == 1)
					.setCostProfile(t.getValue(TASK_HOLDER.COST_PROFILE))
					.setTaskHolderType(TaskHolderType.valueOf(t.getValue(TASK_HOLDER.TYPE)))
					.setUserId(t.getValue(TASK_HOLDER.USER_ID));
		}
	}
	
	public static class FullTaskFiller implements Function<TaskRecord, Task> {
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
					.setSourceId(r.getSourceId())
					.setStartDate(r.getStartDate())
					.setStatus(r.getStatus())
					.setTaskHolder(r.getTaskHolder())
					.setWorkgroup(r.getWorkgroup())
					.setNumber(r.getNumber())
					.setCreationUser(r.getCreationUser())
					.setCreationDate(r.getCreationDate())
					.setModificationUser(r.getModificationUser())
					.setModificationDate(r.getModificationDate())
					.setParent(r.getParent());
		}
	}
	
//	private static class TaskRegistryFiller implements Function<Record, Registry> {
//		@Override
//		public Registry apply(Record r) {
//			return new Registry().setId(r.getValue(REGISTRY.ID))
//					.setDomain(r.getValue(REGISTRY.DOMAIN))
//					.setAlias(r.getValue(REGISTRY.ALIAS))
//					.setName(r.getValue(REGISTRY.NAME))
//					.setType(r.getValue(REGISTRY.TYPE));
//		}
//	}
//	
	private static class TaskFilterCustomerFiller implements Function<Record4<Integer, String, String, Byte>, Customer> {
		@Override
		public Customer apply(Record4<Integer, String, String, Byte> r) {
			Customer customer = new Customer();
			customer.setId(r.getValue(REGISTRY.ID));
			customer.setName(r.getValue(REGISTRY.NAME));	
			customer.setAlias(r.getValue(REGISTRY.ALIAS));
			return customer.setStatus(RegistryStatus.values()[r.getValue(CUSTOMER.STATUS)]);
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
					.setSource(TaskSource.valueOf(r.getSource()))
					.setSourceId(r.getSourceId())
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




