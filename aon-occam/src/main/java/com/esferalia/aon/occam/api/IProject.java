package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.ProjectCommercialFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Project;

public interface IProject {
	
	public Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter);
	public Integer insertProject(AONContext ctx, Project project);
	
	public Stream<ProjectCommercial> getProjectCommercialStream(AONContext ctx, ProjectCommercialFilter filter);
	
	public ProjectReservation getProjectReservation(AONContext ctx, ProjectReservationFilter filter);
	public Stream<ProjectReservation> getProjectReservationStream(AONContext ctx, ProjectReservationFilter filter);

}
