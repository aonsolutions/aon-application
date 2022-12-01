package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

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

public interface ITask2 {
		
	public Task getTask(AONContext ctx, TaskFilter filter);
	public Task getTaskAndChilds(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public Stream<Task> getTaskParentOrChildStream(AONContext ctx, TaskFilter filter, boolean excludeDescription);
	public Stream<Task> getTaskParentOrChildStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter);
	public Stream<Task> getTaskAndChildsStream(AONContext ctx, TaskFilter filter, Integer page, Integer perPage);
	public Task saveTask(AONContext ctx, Task task);
	public void deleteTask(AONContext ctx, Integer id);
	public Map<String, Integer> getTaskCount(AONContext ctx, TaskFilter sender, TaskFilter receiver);
	public TaskCounts getTaskGeneralCount(AONContext ctx, Optional<TaskFilter> status, Optional<TaskFilter> workgroup, Optional<TaskFilter> tags);
	
	//TASKWORKFLOW
	public TaskWorkflow getTaskWorkflow(AONContext ctx, TaskWorkflowFilter filter);
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter);
	public Stream<TaskWorkflow> getTaskWorkflowStream(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage);
	public List<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter);
	public List<TaskWorkflow> getTaskWorkflowList(AONContext ctx, TaskWorkflowFilter filter, Integer page, Integer perPage);
	public TaskWorkflow saveTaskWorkflow(AONContext ctx, TaskWorkflow task);
	public void updateTaskWorkflowBetween(AONContext ctx, TaskWorkflowFilter filter);
	public void deleteTaskWorkflow(AONContext ctx, Integer id);
	
	//TASKATTACH
	public TaskAttach getTaskAttach(AONContext ctx, TaskAttachFilter filter);
	public Stream<TaskAttach> getTaskAttachStream(AONContext ctx, TaskAttachFilter filter);
	public LinkedList<TaskAttach> getTaskAttachList(AONContext ctx, TaskAttachFilter filter);
	public TaskAttach saveTaskAttach(AONContext ctx, TaskAttach task);
	public void deleteTaskAttach(AONContext ctx, Integer id);
	
	
	//DAILY_TRACKING
	public DailyTracking getDailyTracking(AONContext ctx, DailyTrackingFilter filter);
	public Stream<DailyTracking> getDailyTrackingStream(AONContext ctx, DailyTrackingFilter filter);
	public Stream<DailyTracking> getDailyTrackingStream(AONContext ctx, DailyTrackingFilter filter, Integer page, Integer perPage);

	public DailyTracking saveDailyTracking(AONContext ctx, DailyTracking dailyTracking);
	public void deleteDailyTracking(AONContext ctx, Integer id);
	
	//------JOB_TYPE
	public Stream<JobType> getJobTypeStream(AONContext ctx, JobTypeFilter filter);
}
