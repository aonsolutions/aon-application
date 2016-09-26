package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITask;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.TagType;
import com.esferalia.aon.occam.impl.jooq.dao.TaskDAO;

public class TaskImpl implements ITask {

	@Override
	public Task getTask(AONContext ctx, TaskFilter filter) {
		return TaskDAO.getTask(ctx, filter);
	}

	@Override
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskStream(ctx, filter, issueFilter));
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
	public Stream<TaskComment> getTaskCommentStream(AONContext ctx, Integer taskId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskCommentStream(ctx, taskId));

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
	public void updateTaskDescription(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskDescription(ctx, task));
	}
	
	@Override
	public void updateTaskUser(AONContext ctx, Task task) {
		 ctx.getDslContext().transaction(configuration -> TaskDAO.updateTaskUser(ctx, task));
	}
	
	@Override
	public TaskComment getTaskComment(AONContext ctx, Integer taskCommentId) {
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskComment(ctx, taskCommentId));	
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
	public Stream<Registry> getTaskRegistryStream(AONContext ctx){
		return ctx.getDslContext().transactionResult(
				configuration -> TaskDAO.getTaskRegistryStream(ctx));	

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
}
