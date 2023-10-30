package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ITask2;
import com.esferalia.aon.occam.api.model.Filter.DailyTrackingFilter;
import com.esferalia.aon.occam.api.model.Filter.JobTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskWorkflowFilter;
import com.esferalia.aon.occam.api.model.task.DailyTracking;
import com.esferalia.aon.occam.api.model.task.JobType;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskCounts;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.impl.jooq.dao.DailyTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.JobTypeDAO;
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
	public Task getTaskAndChilds(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getTaskAndChilds(ctx, filter));
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
	public Stream<Task> getTaskParentOrChildStream(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskDAO.getParentOrChildStream(ctx, filter));	
	}
	
	@Override
	public Stream<Task> getTaskParentOrChildStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getParentOrChildStream(ctx, filter, page, perPage));	
	}
	
	@Override
	public Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getTaskAndChildsStream(ctx, filter));
	}
	
	@Override
	public Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.getTaskAndChildsStream(ctx, filter, page, perPage));
	}

	@Override
	public Task saveTask(AONContext ctx, Task task) {
		return ctx.getDslContext().transactionResult(configuration -> 
			TaskDAO.save(ctx, task));
	}	
	
	@Override
	public Map<String, Integer> getTaskCount(AONContext ctx,TaskFilter sender, TaskFilter receiver) {
		return ctx.getDslContext().transactionResult(configuration -> TaskDAO.getTaskCount(ctx, sender, receiver));
	}	
	
	@Override
	public TaskCounts getTaskGeneralCount(AONContext ctx, Optional<TaskFilter> status, Optional<TaskFilter> workgroup, Optional<TaskFilter> tags) {
		return ctx.getDslContext().transactionResult(configuration -> TaskDAO.getTaskGeneralCount(ctx, status, workgroup, tags));
	}
	
	@Override
	public Integer getTaskCountFilter(AONContext ctx, TaskFilter taskFilter) {
		return ctx.getDslContext().transactionResult(configuration -> TaskDAO.getTaskCountFilter(ctx, taskFilter));
	}
	
	@Override
	public void deleteTask(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> TaskDAO.delete(ctx, id));
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
	public void updateTaskWorkflowBetween(AONContext ctx, TaskWorkflowFilter filter) {
		ctx.getDslContext().transaction(configuration -> TaskWorkflowDAO.updateTaskWorkflowBetween(ctx, filter));
	}
	
	
	@Override
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getStream(ctx, filter, page, perPage));	
	}

	@Override
	public List<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getList(ctx, filter));
	}
	
	@Override
	public List<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.getList(ctx, filter, page, perPage));
	}

	@Override
	public TaskWorkflow saveTaskWorkflow(AONContext ctx, TaskWorkflow task) {
		return ctx.getDslContext().transactionResult(configuration -> 
		TaskWorkflowDAO.save(ctx, task));
	}	
	
	@Override
	public void deleteTaskWorkflow(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> TaskWorkflowDAO.delete(ctx, id));
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
	public void deleteTaskAttach(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> TaskAttachDAO.delete(ctx, id));
	}

	@Override
	public TaskAttach saveTaskAttach(AONContext ctx, TaskAttach task) {
		return ctx.getDslContext().transactionResult(configuration -> TaskAttachDAO.save(ctx, task));
	}	
	//END----------------TASKATTACH
	
	
	//------- DAILY_TRACKING-------------------
	@Override
	public DailyTracking getDailyTracking(AONContext ctx, DailyTrackingFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		DailyTrackingDAO.get(ctx, filter));
	}

	@Override
	public Stream<DailyTracking> getDailyTrackingStream(AONContext ctx, DailyTrackingFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> 
		DailyTrackingDAO.getStream(ctx, filter));	
	}
	
	@Override
	public Stream<DailyTracking> getDailyTrackingStream(AONContext ctx, DailyTrackingFilter filter, Integer page, Integer perPage) {
		return ctx.getDslContext().transactionResult(configuration -> 
		DailyTrackingDAO.getStream(ctx, filter, page, perPage));	
	}

	@Override
	public DailyTracking saveDailyTracking(AONContext ctx, DailyTracking dailyTracking) {
		return ctx.getDslContext().transactionResult(configuration -> DailyTrackingDAO.save(ctx, dailyTracking));
	}	
	
	
	@Override
	public void deleteDailyTracking(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(configuration -> DailyTrackingDAO.delete(ctx, id));
	}
	//-------- END DAILY_TRACKING------------
	
	
	//---------JOB_TYPE----------
	@Override
	public Stream<JobType> getJobTypeStream(AONContext ctx, JobTypeFilter filter) {
		return ctx.getDslContext().transactionResult(configuration -> JobTypeDAO.getStream(ctx, filter));	
	}
}
