package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.security.TaskHolderWorkgroup;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class TaskHolderWorkgroupValidation {
	
	public static BiConsumer<AONContext, TaskHolderWorkgroup> EMPTY_DOMAIN = (ctx, taskHolderWorkgroup) -> {
		if(taskHolderWorkgroup.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static BiConsumer<AONContext, TaskHolderWorkgroup> EMPTY_TASK_HOLDER = (ctx, taskHolderWorkgroup) -> {
		if(taskHolderWorkgroup.getTaskHolder() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("task_holder"));
	};
	
	public static BiConsumer<AONContext, TaskHolderWorkgroup> EMPTY_WORKGROUP = (ctx, taskHolderWorkgroup) -> {
		if(taskHolderWorkgroup.getWorkgroup().isEmpty() || taskHolderWorkgroup.getWorkgroup().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("workgroup"));
	};
	
//	public static BiConsumer<AONContext, TaskHolderWorkgroup> EXIST_USER_WORKGROUP = (ctx, taskHolderWorkgroup) -> {
//		TaskHolderWorkgroup uw = TaskHolderWorkgroupDAO.get(ctx, f -> f.getTaskHolderProperty().eq(taskHolderWorkgroup.getTaskHolder())
//				.and(f.getWorkgroupProperty().eq(taskHolderWorkgroup.getWorkgroup().getId())));
//		if(!uw.isEmpty()) 
//			throw new AonCoreException(AonError.EXIST_USER_WORKGROUP.getMessage());
//	};
	
	public static void validate(AONContext ctx, TaskHolderWorkgroup taskHolderWorkgroup) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_TASK_HOLDER)
		.andThen(EMPTY_WORKGROUP)
//		.andThen(EXIST_USER_WORKGROUP)
		.accept(ctx, taskHolderWorkgroup);
	}
	
}
