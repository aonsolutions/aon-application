package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectValidation {

	private ProjectValidation() {

	}
	
	public static final BiConsumer<AONContext, Project> EMPTY_DOMAIN = (ctx, project) -> {
		if(project.getDomain() == null || project.getDomain().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("domain"));
	};
	
	public static final BiConsumer<AONContext, Project> EMPTY_REGISTRY = (ctx, project) -> {
		if(project.getRegistry() == null || project.getRegistry().getId() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("registry"));
	};
	
	public static final BiConsumer<AONContext, Project> EMPTY_DATE = (ctx, project) -> {
		if(project.getDate() == null) 
			throw new AonCoreException(AonError.EMPTY_DATA.format("date"));
	};

	public static void validate(AONContext ctx, Project project) throws AonCoreException{
		EMPTY_DOMAIN
		.andThen(EMPTY_REGISTRY)
		.andThen(EMPTY_DATE)
		.accept(ctx, project);
	}
	
}
