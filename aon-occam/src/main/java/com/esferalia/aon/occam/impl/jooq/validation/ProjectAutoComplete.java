package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.Date;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectHolderDAO;
import com.esferalia.aon.watson.error.AonCoreException;

public class ProjectAutoComplete {
	
	private ProjectAutoComplete() {
		
	}
	
	public static final BiConsumer<AONContext, Project> COMPLETE_DATE = (ctx, project) -> {
		if(project.getDate() == null) {
			project.setDate(new Date());
		}
	};
	
	public static final BiConsumer<AONContext, Project> CHECK_REPEAT_PROJECT_TYPE = (ctx, project) -> {
		ProjectHolder projectHolder = project.getProjectHolder();	
		
		if(
			project.getId()==null &&
			(projectHolder.getWorkgroup().getId()!=null || projectHolder.getTaskHolder().getId()!=null)
		) {
			Domain domain = project.getDomain();
			Registry registry = project.getRegistry();
			ProjectType type = project.getType();
			String name = project.getName();
			
			Project p = ProjectDAO.get(ctx, f-> 
				f.getDomainProperty().eq(domain.getId())
				.and(f.getRegistryProperty().eq(registry.getId()))
				.and(f.getNameProperty().eq(name))
				.and(f.getProjectTypeProperty().eq(type.getId()))
			);
			
			if(!p.isEmpty()) {
				ProjectHolder holder = ProjectHolderDAO.get(ctx, 
					f->f.getDomainProperty().eq(p.getDomain().getId())
					 .and(
						 f.getProjectProperty().eq(p.getId())
						 .and(f.getEndDateProperty().isNull())
					 )
				);
	
				if(!holder.isEmpty()) {
					p.setProjectHolder(holder);
	
					p.getProjectHolder().setWorkgroup(projectHolder.getWorkgroup());
					p.getProjectHolder().setTaskHolder(projectHolder.getTaskHolder());
				}
				
				project.setValues(p);
			} 
		}
	};


	public static void autoComplete(AONContext ctx, Project project) throws AonCoreException {
		COMPLETE_DATE
		.andThen(CHECK_REPEAT_PROJECT_TYPE)
			.accept(ctx, project);
	}

}
