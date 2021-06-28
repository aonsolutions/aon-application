package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITask2;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.impl.jooq.dao.TaskDAO;

public class Task2Impl implements ITask2 {

	@Override
	public Task getTask(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.get(ctx, filter));
	}

	@Override
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskDAO.getStream(ctx, filter));	
	}
	
	@Override
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getStream(ctx, filter, page, perPage));	
	}

	@Override
	public LinkedList<Task> getTaskList(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getList(ctx, filter));
	}
	
	@Override
	public LinkedList<Task> getTaskList(AONContext ctx, TaskFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getList(ctx, filter, page, perPage));
	}

	@Override
	public Task saveTask(AONContext ctx, Task task) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.save(ctx, task));
	}	
	
}
