package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITask2;
import com.esferalia.aon.occam.api.model.Filter.TaskAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskWorkflowFilter;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.impl.jooq.dao.TaskAttachDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TaskWorkflowDAO;

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
	
	//START ------------TASKWORKFLOW
	@Override
	public TaskWorkflow getTaskWorkflow(AONContext ctx, TaskWorkflowFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.get(ctx, filter));
	}

	@Override
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getStream(ctx, filter));	
	}
	
	@Override
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getStream(ctx, filter, page, perPage));	
	}

	@Override
	public LinkedList<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getList(ctx, filter));
	}
	
	@Override
	public LinkedList<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getList(ctx, filter, page, perPage));
	}

	@Override
	public TaskWorkflow saveTaskWorkflow(AONContext ctx, TaskWorkflow task) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.save(ctx, task));
	}	
	//END----------------TASKWORKFLOW
	
	
	//START ------------TASKATTACH
	@Override
	public TaskAttach getTaskAttach(AONContext ctx, TaskAttachFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskAttachDAO.get(ctx, filter));
	}

	@Override
	public Stream<TaskAttach> getTaskAttachStream(AONContext ctx, TaskAttachFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskAttachDAO.getStream(ctx, filter));	
	}
	
	@Override
	public LinkedList<TaskAttach> getTaskAttachList(AONContext ctx, TaskAttachFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskAttachDAO.getList(ctx, filter));
	}
	
	@Override
	public void deleteTaskAttach(AONContext ctx, TaskAttachFilter filter) {
		ctx.getDslContext().transaction(configuration -> TaskAttachDAO.delete(ctx, filter));
	}

	@Override
	public TaskAttach saveTaskAttach(AONContext ctx, TaskAttach task) {
		return ctx.getDslContext().transactionResult(configuration -> TaskAttachDAO.save(ctx, task));
	}	
	//END----------------TASKATTACH
}
