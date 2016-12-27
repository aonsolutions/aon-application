package com.esferalia.aon.occam.impl.jooq;

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
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.TaskDAO;

public class TaskImpl implements ITask {
	
	@Override
	public Boolean isTaskParent(AONContext ctx, Integer parentId) {
		return ctx.getDslContext().transactionResult(configuration -> TaskDAO.isTaskParent(ctx, parentId));
	}
	
	@Override
	public Task getTask(AONContext ctx, TaskFilter filter) {
		return TaskDAO.getTask(ctx, filter);
	}

	@Override
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskStream(ctx, filter));
	}
	
	@Override
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskStream(ctx, filter, issueFilter));
	}
	
	@Override
	public Stream<Task> getDuplicateTaskStream(AONContext ctx, Integer parent) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getDuplicateTaskStream(ctx, parent));
	}
	
	@Override
	public Integer[] getTaskCount(AONContext ctx, TaskFilter filter, IssueFilter issueFilter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskCount(ctx, filter, issueFilter));
	}
	
	@Override
	public Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskLabelStream(ctx, filter));
	}

	@Override
	public Integer getCommentsCount(AONContext ctx, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getCommentsCount(ctx, taskId));
	}
	
	@Override
	public Stream<TaskComment> getTaskCommentStream(AONContext ctx, TaskCommentFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskCommentStream(ctx, filter));
	}
	
	@Override
	public Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskEventStream(ctx, taskId));

	}
	
	@Override
	public Integer getLastTaskNumber(AONContext ctx) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getLastTaskNumber(ctx));
	}
	
	@Override
	public Integer createTask(AONContext ctx, Task task) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.createTask(ctx, task));
	}

	@Override
	public void updateTaskStatus(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskStatus(ctx, task));
	}
	
	@Override
	public void updateTaskParent(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskParent(ctx, task));
	}
	
	@Override
	public void updateTaskDescription(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskDescription(ctx, task));
	}
	
	@Override
	public void updateTaskTitle(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskTitle(ctx, task));
	}
	
	@Override
	public void updateTaskPriority(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskPriority(ctx, task));
	}
	
	@Override
	public void updateTaskUser(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskUser(ctx, task));
	}

	@Override
	public TaskComment getLastTaskComment(AONContext ctx, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getLastTaskComment(ctx, taskId));	
	}
	
	@Override
	public TaskComment createTaskComment(AONContext ctx, TaskComment taskComment, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.createTaskComment(ctx, taskComment, taskId));	
	}

	@Override
	public TaskComment updateTaskComment(AONContext ctx, TaskComment taskComment) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.updateTaskComment(ctx, taskComment));	
	}

	@Override
	public TaskEvent getTaskEvent(AONContext ctx, Integer taskEventId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskEvent(ctx, taskEventId));	
	}
	
	@Override
	public TaskEvent getLastTaskEvent(AONContext ctx, TaskEventFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getLastTaskEvent(ctx, filter));	
	}
	
	@Override
	public TaskEvent getTaskEvent(AONContext ctx, TaskEventFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskEvent(ctx, filter));	
	}

	@Override
	public TaskEvent createTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.createTaskEvent(ctx, taskEvent, taskId));	
	}

	@Override
	public TaskEvent updateTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskEventId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.updateTaskEvent(ctx, taskEvent, taskEventId));	
	}
	
	@Override
	public Stream<Registry> getTaskMemberStream(AONContext ctx, String filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskMemberStream(ctx, filter));	
	}
	
	@Override
	public Stream<Registry> getTaskMemberWStream(AONContext ctx, String filter, Integer workgroupId){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskMemberWStream(ctx, filter,workgroupId));	
	}
	
	@Override
	public Stream<Customer> getTaskCustomerStream(AONContext ctx){
		return ctx.getDslContext().transactionResult(
			configuration -> TaskDAO.getTaskCustomerStream(ctx));	
	}
	
	@Override
	public Stream<Workgroup> getTaskWorkgroupStream(AONContext ctx, String filter){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskWorkgroupStream(ctx, filter));	
	}

	@Override
	public void deleteTaskTag(AONContext ctx, Integer taskId, TagType tagType) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.deleteTaskTag(ctx, taskId, tagType));		
	}

	@Override
	public void createTaskTag(AONContext ctx, TaskTag taskTag) {
		ctx.getDslContext().transaction(configuration -> TaskDAO.createTaskTag(ctx, taskTag));
	}
	
	@Override
	public Workgroup getWorkgroup(AONContext ctx, Integer wId){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getWorkgroup(ctx, wId));	
	}

	@Override
	public void deleteTaskTag(AONContext ctx, TaskTagFilter filter) {
		ctx.getDslContext().transaction(configuration -> TaskDAO.deleteTaskTag(ctx, filter));		
	}

	@Override
	public Stream<Customer> getFilterCustomerStream(AONContext ctx, String filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getFilterCustomerStream(ctx, filter));	
	}

	@Override
	public void deleteTaskComment(AONContext ctx, TaskCommentFilter filter) {
		ctx.getDslContext().transaction(configuration -> 
				TaskDAO.deleteTaskComment(ctx, filter));
	}

	@Override
	public Workgroup insertWorkgroup(AONContext ctx, Workgroup workgroup) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.insertWorkgroup(ctx, workgroup));
	}

	@Override
	public Workgroup updateWorkgroup(AONContext ctx, Workgroup workgroup) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.updateWorkgroup(ctx, workgroup));
	}

	@Override
	public Workgroup deleteWorkgroup(AONContext ctx, Integer wId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.deleteWorkgroup(ctx, wId));
	}

	@Override
	public TaskHolder getTaskHolder(AONContext ctx, TaskHolderFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskHolder(ctx, filter));
	}
	
	@Override
	public TaskHolder insertTaskHolder(AONContext ctx, TaskHolder taskHolder) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.insertTaskHolder(ctx, taskHolder));
	}

	@Override
	public TaskHolder deleteTaskHolder(AONContext ctx, Integer taskHolder) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.deleteTaskHolder(ctx, taskHolder));
	}

	@Override
	public Stream<Workgroup> getTaskHolderWorkgroupStream(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskHolderWorkgroupStream(ctx, filter));
	}
	
	@Override
	public Boolean isTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.isTaskHolderWorkgroup(ctx, filter));
	}

	@Override
	public void insertTaskHolderWorkgroup(AONContext ctx, Integer taskHolder, Integer workgroup) {
		ctx.getDslContext().transaction(
				configuration -> TaskDAO.insertTaskHolderWorkgroup(ctx, taskHolder, workgroup));
	}

	@Override
	public void deleteTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter) {
		ctx.getDslContext().transaction(
				configuration -> TaskDAO.deleteTaskHolderWorkgroup(ctx, filter));
	}
}
