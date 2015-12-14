package com.code.aon.google.apis.jooq;

import static com.esferalia.aon.jooq.tables.Task.TASK;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.LinkedList;

import org.jooq.Result;

import com.esferalia.aon.google.sql.AbstractSQL.Task;
import com.esferalia.aon.jooq.tables.records.TaskRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.security.User;

public class DBTask {
	
	public static Project getProjectTask(Domain domain, User user, Task task){
		return AON.getProject(domain.getName(), domain.getId(), user.getLogin(),
				f -> f.getIdProperty().eq(task.getProject()));
	}

	public static LinkedList<Task> getTask(Domain domain, User user){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			Result<TaskRecord> result = ctx.getDslContext().select()
				.from(TASK).join(USER).on(TASK.TASK_HOLDER.eq(USER.REGISTRY))
				.where(USER.LOGIN.eq(user.getLogin()))
				.fetchInto(TASK);			
			LinkedList<Task> taskList = new LinkedList<Task>();;
			for (TaskRecord tr : result) {
				Task task = new Task();
				task.setActivityType(tr.getActivityType());
				task.setComments(tr.getComments());
				task.setDescription(tr.getDescription());
				task.setDomain(tr.getDomain());
				task.setDueDate(tr.getDueDate());
				task.setEndDate(tr.getEndDate());
				task.setGtaskId(tr.getGtaskId());
				task.setGtasklistId(tr.getGtasklistId());
				task.setId(tr.getId());
				task.setPercent((short)tr.getPercent());
				task.setPriority((short)tr.getPriority());
				task.setProject(tr.getProject());
				task.setRegistry(tr.getRegistry());
				task.setRepeatPeriod((short)tr.getRepeatPeriod());
				task.setSender(tr.getSender());
				task.setSource((short)tr.getSource());
				task.setStartDate(tr.getStartDate());
				task.setStatus((short)tr.getStatus());
				task.setTaskHolder(tr.getTaskHolder());
				task.setWorkgroup(tr.getWorkgroup());
				taskList.add(task);
			}
			return taskList;
		} finally  {
			if(ctx != null) ctx.close();
		}
	}
	
	public static void addTaskId(Domain domain, User user, String taskId,Integer id){
		AONContext ctx = null;
		try{
			ctx =AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			
			ctx.getDslContext().update(TASK)
			.set(TASK.GTASK_ID, taskId)
			.where(TASK.ID.eq(id))
			.execute();
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}

	public static void addTaskListId(Domain domain, User user, String taskListId,Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());

			ctx.getDslContext().update(TASK)
			.set(TASK.GTASKLIST_ID, taskListId)
			.where(TASK.ID.eq(id))
			.execute();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}
	
}
