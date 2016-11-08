package com.esferalia.aon.occam.api;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Project;

public interface IProject {
	
	public Project getProject(AONContext ctx, ProjectFilter filter);
	public LinkedList<Project> getProjectList(AONContext ctx, ProjectFilter filter);
	public Integer insertProject(AONContext ctx, Project project);
	
	public ProjectReservation getProjectReservation(AONContext ctx, Integer projectId);
	public Stream<ProjectReservation> getProjectReservationStream(AONContext ctx, ProjectReservationFilter filter);

}
