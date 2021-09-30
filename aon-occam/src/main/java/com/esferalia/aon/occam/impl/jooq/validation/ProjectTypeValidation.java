package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ProjectTypeValidation {

	private ProjectTypeValidation() {

	}
	
	public static final BiConsumer<AONContext, ProjectType> EMPTY_DOMAIN = (ctx, projectType) -> {
		if(projectType.getDomain() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};

	public static final BiConsumer<AONContext, ProjectType> EMPTY_DESCRIPTION = (ctx, projectType) -> {
		if(AonStringUtils.isBlank(projectType.getDescription())) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("description"));
	};

	public static void validate(AONContext ctx, ProjectType projectType) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_DESCRIPTION)
		.accept(ctx, projectType);
	}
	
}
