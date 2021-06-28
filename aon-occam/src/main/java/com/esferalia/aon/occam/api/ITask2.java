package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.task.Task;

public interface ITask2 {
		
	public Task getTask(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public LinkedList<Task> getTaskList(AONContext ctx, TaskFilter filter);
	public LinkedList<Task> getTaskList(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public Task saveTask(AONContext ctx, Task task);

}
