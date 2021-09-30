package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectHolderValidation {

	private ProjectHolderValidation() {

	}
	
	public static final BiConsumer<AONContext, ProjectHolder> EMPTY_DOMAIN = (ctx, projectHolder) -> {
		if(projectHolder.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};

	public static final BiConsumer<AONContext, ProjectHolder> EMPTY_PROJECT = (ctx, projectHolder) -> {
		if(projectHolder.getProject() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("project"));
	};
	
	public static final BiConsumer<AONContext, ProjectHolder> EMPTY_START_DATE = (ctx, projectHolder) -> {
		if(projectHolder.getStartDate() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("start date"));
	};
	
	public static final BiConsumer<AONContext, ProjectHolder> EMPTY_WORKGROUP_TASK_HOLDER = (ctx, projectHolder) -> {
		if((projectHolder.getWorkgroup() == null || projectHolder.getWorkgroup().getId() == null)
				&& (projectHolder.getTaskHolder() == null || projectHolder.getTaskHolder().getId() == null)) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("workgroup / task holder"));
	};
	

	public static void validate(AONContext ctx, ProjectHolder projectHolder) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_PROJECT)
		.andThen(EMPTY_START_DATE)
		.andThen(EMPTY_WORKGROUP_TASK_HOLDER)
		.accept(ctx, projectHolder);
	}
	
}
