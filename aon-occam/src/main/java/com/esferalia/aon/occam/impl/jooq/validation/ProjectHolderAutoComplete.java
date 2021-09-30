package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectHolderAutoComplete {
	
	private ProjectHolderAutoComplete() {
		
	}
	
	public static final BiConsumer<AONContext, ProjectHolder> COMPLETE_START_DATE = (ctx, projectHolder) -> {
		if(projectHolder.getStartDate() == null) {
			projectHolder.setStartDate(new Date());
		}
	};

	public static void autoComplete(AONContext ctx, ProjectHolder projectHolder) throws AonCoreException {
		COMPLETE_START_DATE
			.accept(ctx, projectHolder);
	}

}
