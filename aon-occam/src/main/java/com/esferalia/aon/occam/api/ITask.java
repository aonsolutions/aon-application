package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.TaskEventFilter;
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

public interface ITask {
	public Task getTask(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter);
	public Integer[] getTaskCount(AONContext ctx, TaskFilter filter, IssueFilter issueFilter);
	public Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter);
	public Integer getCommentsCount(AONContext ctx, Integer taskId);
	public Stream<TaskComment> getTaskCommentStream(AONContext ctx, Integer taskId);
	public Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId);
	public Integer getLastTaskNumber(AONContext ctx);
	
	public Integer createTask(AONContext ctx, Task task);
	public void updateTaskStatus(AONContext ctx, Task task);
	public void updateTaskUser(AONContext ctx, Task task);
	public void updateTaskDescription(AONContext ctx, Task task);
	public void updateTaskPriority(AONContext ctx, Task task);

	
	public TaskComment getTaskComment(AONContext ctx, Integer taskCommentId);
	public TaskComment getLastTaskComment(AONContext ctx, Integer taskId);

	public TaskComment createTaskComment(AONContext ctx, TaskComment taskComment, Integer taskId);
	public TaskComment updateTaskComment(AONContext ctx, TaskComment taskComment);
	public TaskEvent getTaskEvent(AONContext ctx, Integer taskEventId);
	public TaskEvent getLastTaskEvent(AONContext ctx, Integer taskId);
	public TaskEvent getTaskEvent(AONContext ctx,TaskEventFilter filter);
	public TaskEvent createTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskId);
	public TaskEvent updateTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskEventId);
	
	public Stream<Registry> getTaskMemberStream(AONContext ctx, String filter);
	public Stream<Registry> getTaskRegistryStream(AONContext ctx);
	public Stream<Workgroup> getTaskWorkgroupStream(AONContext ctx, String filter);
	
	public Stream<Registry> getFilterRegistryStream(AONContext ctx, String filter);
	
	public void deleteTaskTag(AONContext ctx, Integer taskId, TagType tagType);
	public void createTaskTag(AONContext ctx, TaskTag taskTag);
	public void deleteTaskTag(AONContext ctx, TaskTagFilter filter);

	
	public Workgroup getWorkgroup(AONContext ctx, Integer wId); 
	public Stream<Registry> getTaskMemberWStream(AONContext ctx, String filter, Integer workgroupId);
	

}
