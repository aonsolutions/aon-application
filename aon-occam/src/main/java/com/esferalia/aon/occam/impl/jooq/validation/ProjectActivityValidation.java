package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectActivityValidation {

	private ProjectActivityValidation() {

	}
	
	public static final BiConsumer<AONContext, ProjectActivity> EMPTY_DOMAIN = (ctx, projectActivity) -> {
		if(projectActivity.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};

	public static final BiConsumer<AONContext, ProjectActivity> EMPTY_PROJECT = (ctx, projectActivity) -> {
		if(projectActivity.getProject() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("project"));
	};
	
	public static final BiConsumer<AONContext, ProjectActivity> EMPTY_ACTIVITY_TYPE = (ctx, projectActivity) -> {
		if(projectActivity.getActivityType() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("activityType"));
	};

	

	public static void validate(AONContext ctx, ProjectActivity projectActivity) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_PROJECT)
		.andThen(EMPTY_ACTIVITY_TYPE)
		.accept(ctx, projectActivity);
	}
	
}
