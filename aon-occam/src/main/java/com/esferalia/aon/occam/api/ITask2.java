package com.esferalia.aon.occam.api;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.TaskAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskWorkflowFilter;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;

public interface ITask2 {
		
	public Task getTask(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public LinkedList<Task> getTaskList(AONContext ctx, TaskFilter filter);
	public LinkedList<Task> getTaskList(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public Task saveTask(AONContext ctx, Task task);
	public void deleteTask(AONContext ctx, Integer id);
	public HashMap<Byte, Integer> getTaskStatusCount(AONContext ctx, TaskFilter filter);
	public HashMap<String, Integer> getTaskCount(AONContext ctx, TaskFilter filter, Integer taskHolderId);
	
	//TASKWORKFLOW
	public TaskWorkflow getTaskWorkflow(AONContext ctx, TaskWorkflowFilter filter);
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter);
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage);
	public LinkedList<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter);
	public LinkedList<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage);
	public TaskWorkflow saveTaskWorkflow(AONContext ctx, TaskWorkflow task);
	public void updateTaskWorkflowBetween(AONContext ctx, TaskWorkflowFilter filter);
	
	//TASKATTACH
	public TaskAttach getTaskAttach(AONContext ctx, TaskAttachFilter filter);
	public Stream<TaskAttach> getTaskAttachStream(AONContext ctx, TaskAttachFilter filter);
	public LinkedList<TaskAttach> getTaskAttachList(AONContext ctx, TaskAttachFilter filter);
	public TaskAttach saveTaskAttach(AONContext ctx, TaskAttach task);
	public void deleteTaskAttach(AONContext ctx, Integer id);
}
