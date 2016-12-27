package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

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

public interface ITask {
	
	public Boolean isTaskParent(AONContext ctx, Integer parentId);

	public Task getTask(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, IssueFilter issueFilter);
	public Stream<Task> getDuplicateTaskStream(AONContext ctx, Integer parent);
	public Integer[] getTaskCount(AONContext ctx, TaskFilter filter, IssueFilter issueFilter);
	public Stream<Tag> getTaskLabelStream(AONContext ctx, TaskTagFilter filter);
	public Integer getCommentsCount(AONContext ctx, Integer taskId);
	public Stream<TaskComment> getTaskCommentStream(AONContext ctx, TaskCommentFilter filter);
	public Stream<TaskEvent> getTaskEventStream(AONContext ctx, Integer taskId);
	public Integer getLastTaskNumber(AONContext ctx);
	
	public Integer createTask(AONContext ctx, Task task);
	public void updateTaskStatus(AONContext ctx, Task task);
	public void updateTaskParent(AONContext ctx, Task task);
	public void updateTaskUser(AONContext ctx, Task task);
	public void updateTaskTitle(AONContext ctx, Task task);
	public void updateTaskDescription(AONContext ctx, Task task);
	public void updateTaskPriority(AONContext ctx, Task task);

	public TaskComment getLastTaskComment(AONContext ctx, Integer taskId);

	public TaskComment createTaskComment(AONContext ctx, TaskComment taskComment, Integer taskId);
	public TaskComment updateTaskComment(AONContext ctx, TaskComment taskComment);
	public TaskEvent getTaskEvent(AONContext ctx, Integer taskEventId);
	public TaskEvent getLastTaskEvent(AONContext ctx, TaskEventFilter filter);
	public TaskEvent getTaskEvent(AONContext ctx,TaskEventFilter filter);
	public TaskEvent createTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskId);
	public TaskEvent updateTaskEvent(AONContext ctx, TaskEvent taskEvent, Integer taskEventId);
	
	public Stream<Registry> getTaskMemberStream(AONContext ctx, String filter);
	public Stream<Customer> getTaskCustomerStream(AONContext ctx);
	public Stream<Workgroup> getTaskWorkgroupStream(AONContext ctx, String filter);
	
	public Stream<Customer> getFilterCustomerStream(AONContext ctx, String filter);
	
	public void deleteTaskTag(AONContext ctx, Integer taskId, TagType tagType);
	public void createTaskTag(AONContext ctx, TaskTag taskTag);
	public void deleteTaskTag(AONContext ctx, TaskTagFilter filter);

	public void deleteTaskComment(AONContext ctx, TaskCommentFilter filter);
	
	public Workgroup getWorkgroup(AONContext ctx, Integer wId); 
	public Workgroup insertWorkgroup(AONContext ctx, Workgroup workgroup); 
	public Workgroup updateWorkgroup(AONContext ctx, Workgroup workgroup); 
	public Workgroup deleteWorkgroup(AONContext ctx, Integer wId); 
	public Stream<Registry> getTaskMemberWStream(AONContext ctx, String filter, Integer workgroupId);
	
	public TaskHolder getTaskHolder(AONContext ctx, TaskHolderFilter filter);
	public TaskHolder insertTaskHolder(AONContext ctx, TaskHolder taskHolder);
	public TaskHolder deleteTaskHolder(AONContext ctx, Integer taskHolder);
	
	public Stream<Workgroup> getTaskHolderWorkgroupStream(AONContext ctx, TaskHolderWorkgroupFilter filter);
	public Boolean isTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter);
	public void insertTaskHolderWorkgroup(AONContext ctx, Integer taskHolder, Integer workgroup);
	public void deleteTaskHolderWorkgroup(AONContext ctx, TaskHolderWorkgroupFilter filter);
	

}
