package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectActivityDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectActivityAutoComplete {
	
	private ProjectActivityAutoComplete() {
		
	}
	
	public static final BiConsumer<AONContext, ProjectActivity> NOT_REPEAT_DOMAIN_PROJECT_ACTIVITY_TYPE = (ctx, projectActivity) -> {
		if(projectActivity.getDomain()!=null && projectActivity.getProject()!=null && projectActivity.getActivityType().getId()!=null) {
			ProjectActivityDAO.get(ctx, 
				f-> f.getDomainProperty().eq(projectActivity.getDomain())
				.and(f.getProjectProperty().eq(projectActivity.getProject()))
				.and(f.getActivityTypeProperty().eq(projectActivity.getActivityType().getId()))
			)
			.ifPresent(p->
				projectActivity.setId(p.getId())
			);
		}
	};

	public static void autoComplete(AONContext ctx, ProjectActivity projectActivity) throws AonCoreException {
		NOT_REPEAT_DOMAIN_PROJECT_ACTIVITY_TYPE
			.accept(ctx, projectActivity);
	}

}
