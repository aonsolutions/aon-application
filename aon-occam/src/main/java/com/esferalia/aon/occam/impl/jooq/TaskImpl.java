package com.esferalia.aon.occam.impl.jooq;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITask;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Filter.TaskCommentFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskEventFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskHolderWorkgroupFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
import com.esferalia.aon.occam.api.model.OldTask;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskHolderWorkgroupDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskOldDAO;

public class TaskImpl implements ITask {
	
	@Override
	public Boolean isTaskParent(AONContext ctx, Integer parentId) {
		return ctx.getDslContext().transactionResult(configuration -> TaskOldDAO.isTaskParent(ctx, parentId));
	}
	
	@Override
	public OldTask getTask(AONContext ctx, TaskFilter filter) {
		return TaskOldDAO.getTask(ctx, filter);
	}

	@Override
	public Stream<OldTask> getTaskStream(AONContext ctx, TaskFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskStream(ctx, filter));
	}
	
	@Override
	public Stream<OldTask> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskStream(ctx, filter, issueFilter));
	}
	
	@Override
	public Stream<OldTask> getDuplicateTaskStream(AONContext ctx, Integer parent) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getDuplicateTaskStream(ctx, parent));
	}
	
	@Override
	public Integer[] getTaskCount(AONContext ctx, TaskFilter filter, IssueFilter issueFilter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskCount(ctx, filter, issueFilter));
	}
	
	@Override
	public Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskLabelStream(ctx, filter));
	}

	@Override
	public Integer getCommentsCount(AONContext ctx, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getCommentsCount(ctx, taskId));
	}
	
	@Override
	public Stream<TaskComment> getTaskCommentStream(AONContext ctx, TaskCommentFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskCommentStream(ctx, filter));
	}
	
	@Override
	public Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskEventStream(ctx, taskId));

	}
	
	@Override
	public Integer getLastTaskNumber(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getLastTaskNumber(ctx));
	}
	
	@Override
	public Integer createTask(AONContext ctx, OldTask task) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.createTask(ctx, task));
	}

	@Override
	public OldTask updateTask(AONContext ctx, OldTask task) {
		return ctx.getDslContext().transactionResult(configuration -> TaskOldDAO.updateTask(ctx, task));
	}

	@Override
	public TaskComment getLastTaskComment(AONContext ctx, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getLastTaskComment(ctx, taskId));	
	}
	
	@Override
	public TaskComment createTaskComment(AONContext ctx, TaskComment taskComment, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.createTaskComment(ctx, taskComment, taskId));	
	}

	@Override
	public TaskComment updateTaskComment(AONContext ctx, TaskComment taskComment) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.updateTaskComment(ctx, taskComment));	
	}

	@Override
	public TaskEvent getTaskEvent(AONContext ctx, Integer taskEventId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskEvent(ctx, taskEventId));	
	}
	
	@Override
	public TaskEvent getLastTaskEvent(AONContext ctx, TaskEventFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getLastTaskEvent(ctx, filter));	
	}
	
	@Override
	public TaskEvent getTaskEvent(AONContext ctx, TaskEventFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskEvent(ctx, filter));	
	}

	@Override
	public TaskEvent createTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.createTaskEvent(ctx, taskEvent, taskId));	
	}

	@Override
	public TaskEvent updateTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskEventId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.updateTaskEvent(ctx, taskEvent, taskEventId));	
	}
	
	@Override
	public Stream<TaskHolder> getTaskMemberWStream(AONContext ctx, String filter, Integer workgroupId){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskMemberWStream(ctx, filter,workgroupId));	
	}
	
	@Override
	@Deprecated
	public Stream<Workgroup> getTaskWorkgroupStream(AONContext ctx, String filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskWorkgroupStream(ctx, filter));	
	}
	
	@Override
	public Stream<TaskHolder> getTaskHolderWorkgroupStream(AONContext ctx, TaskHolderFilter filter, Integer workgroupId){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskHolderDAO.getTaskHolderWorkgroup(ctx, filter, workgroupId));	
	}

	@Override
	public void saveTaskHolderWorkgroups(AONContext ctx, TaskHolder taskHolder){
		ctx.getDslContext().transaction(
				configuration -> TaskHolderDAO.saveTaskHolderWorkgroups(ctx, taskHolder));	
	}

	@Override
	public List<TaskHolderWorkgroup> getTaskHolderWorkgroupsList(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskHolderWorkgroupDAO.getList(ctx, filter));	
	}
	
	@Override
	public TaskHolderWorkgroup saveTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroup taskHolderWorkgroup) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskHolderWorkgroupDAO.saveTaskHolderWorkgroup(ctx, taskHolderWorkgroup));	
	}
	
	@Override
	public void deleteTaskTag(AONContext ctx, Integer taskId, TagType tagType) {
		 ctx.getDslContext().transaction(configuration -> TaskOldDAO.deleteTaskTag(ctx, taskId, tagType));		
	}

	@Override
	public void createTaskTag(AONContext ctx, TaskTag taskTag) {
		ctx.getDslContext().transaction(configuration -> TaskOldDAO.createTaskTag(ctx, taskTag));
	}
	
	@Override
	public Workgroup getWorkgroup(AONContext ctx, Integer wId){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getWorkgroup(ctx, wId));	
	}

	@Override
	public void deleteTaskTag(AONContext ctx, TaskTagFilter filter) {
		ctx.getDslContext().transaction(configuration -> TaskOldDAO.deleteTaskTag(ctx, filter));		
	}

	@Override
	public Stream<Customer> getFilterCustomerStream(AONContext ctx, String filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getFilterCustomerStream(ctx, filter));	
	}

	@Override
	public void deleteTaskComment(AONContext ctx, TaskCommentFilter filter) {
		ctx.getDslContext().transaction(configuration -> 
				TaskOldDAO.deleteTaskComment(ctx, filter));
	}

	@Override
	public Workgroup insertWorkgroup(AONContext ctx, Workgroup workgroup) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.insertWorkgroup(ctx, workgroup));
	}

	@Override
	public Workgroup updateWorkgroup(AONContext ctx, Workgroup workgroup) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.updateWorkgroup(ctx, workgroup));
	}

	@Override
	public Workgroup deleteWorkgroup(AONContext ctx, Integer wId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.deleteWorkgroup(ctx, wId));
	}

	@Override
	public TaskHolder getTaskHolder(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskHolder(ctx, filter));
	}

	@Override
	public TaskHolder save(AONContext ctx, TaskHolder taskHolder) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskHolderDAO.save(ctx, taskHolder));
	}
	
	@Override
	public TaskHolder updateTaskHolder(AONContext ctx, TaskHolder taskHolder) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.updateTaskHolder(ctx, taskHolder));
	}
	
	@Override
	public TaskHolder insertTaskHolder(AONContext ctx, TaskHolder taskHolder) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.insertTaskHolder(ctx, taskHolder));
	}

	@Override
	public TaskHolder deleteTaskHolder(AONContext ctx, Integer taskHolder) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.deleteTaskHolder(ctx, taskHolder));
	}

	@Override
	public Stream<Workgroup> getTaskHolderWorkgroupStream(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskHolderWorkgroupStream(ctx, filter));
	}
	
	@Override
	public Boolean isTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.isTaskHolderWorkgroup(ctx, filter));
	}

	@Override
	public void insertTaskHolderWorkgroup(AONContext ctx, Integer taskHolder, Integer workgroup) {
		ctx.getDslContext().transaction(
				configuration -> TaskOldDAO.insertTaskHolderWorkgroup(ctx, taskHolder, workgroup));
	}

	@Override
	public void deleteTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		ctx.getDslContext().transaction(
				configuration -> TaskOldDAO.deleteTaskHolderWorkgroup(ctx, filter));
	}

	@Override
	public void deleteTask(AONContext ctx, TaskFilter filter) {
		ctx.getDslContext().transaction(
				configuration -> TaskOldDAO.deleteTask(ctx, filter));		
	}

	@Override
	public void deleteTaskEvent(AONContext ctx, TaskEventFilter filter) {
		ctx.getDslContext().transaction(
				configuration -> TaskOldDAO.deleteTaskEvent(ctx, filter));				
	}

	@Override
	public Stream<TaskHolder> getTaskHolderStream(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskHolderStream(ctx, filter));
	}
	
	@Override
	public long getTaskHolderCount(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskHolderCount(ctx, filter));
	}
	
	@Override
	public Stream<TaskHolder> getTaskHolderEmployee(AONContext ctx, TaskHolderFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskHolderEmployee(ctx, filter, page, perPage));
	}

	@Override
	public List<TaskHolder> getTaskHolderFullList(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskHolderDAO.getTaskHolderFullList(ctx, filter));
	}
	
	@Override
	public Stream<TaskHolder> getTaskHolderStream(AONContext ctx, byte[] auth) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskOldDAO.getTaskHolderStream(ctx, auth));
	}

	@Override
	public List<TaskHolder> getAviableSellerTaskHolders(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskHolderDAO.getAviableSellerTaskHolders(ctx));	
	}
}
