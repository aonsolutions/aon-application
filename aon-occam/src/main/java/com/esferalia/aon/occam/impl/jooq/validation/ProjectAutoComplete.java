package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectAutoComplete {
	
	private ProjectAutoComplete() {
		
	}
	
	public static final BiConsumer<AONContext, Project> COMPLETE_DATE = (ctx, project) -> {
		if(project.getDate() == null) {
			project.setDate(new Date());
		}
	};

	public static void autoComplete(AONContext ctx, Project project) throws AonCoreException {
		COMPLETE_DATE
			.accept(ctx, project);
	}

}
