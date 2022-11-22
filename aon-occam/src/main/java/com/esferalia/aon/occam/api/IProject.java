package com.esferalia.aon.occam.api;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.Filter.ActivityTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectActivityFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectCommercialFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectTypeFilter;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;

public interface IProject {
	
	// ---------- PROJECT
	
	public Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter);
	public Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter, Integer page, Integer perPage);
	public Integer insertProject(AONContext ctx, Project project);
	public Project saveProject(AONContext ctx, Project project);
	public void deleteProject(AONContext ctx, Integer id);
	
	public Integer insertProjectCommercial(AONContext ctx, ProjectCommercial pc);
	
	public Stream<ProjectCommercial> getProjectCommercialStream(AONContext ctx, ProjectCommercialFilter filter);
	
	public ProjectReservation getProjectReservation(AONContext ctx, ProjectReservationFilter filter);
	public Stream<ProjectReservation> getProjectReservationStream(AONContext ctx, ProjectReservationFilter filter);
	public void fixProjectCommercial(AONContext ctx);
	
	
	// ---------- PROJECT TYPE
	
	public ProjectType getProjectType(AONContext ctx, ProjectTypeFilter filter);
	public Stream<ProjectType> getProjectTypeStream(AONContext ctx, ProjectTypeFilter filter);
	public ProjectType saveProjectType(AONContext ctx, ProjectType projectType);
	public void deleteProjectType(AONContext ctx, Integer id);
	
	
	// ---------- ACTIVITY TYPE
	
	public ActivityType getActivityType(AONContext ctx, ActivityTypeFilter filter);
	public Stream<ActivityType> getActivityTypeStream(AONContext ctx, ActivityTypeFilter filter);
	public ActivityType saveActivityType(AONContext ctx, ActivityType activityType);
	public void deleteActivityType(AONContext ctx, Integer id);

	// ---------- PROJECT HOLDER
	public ProjectHolder getProjectHolder(AONContext ctx, ProjectHolderFilter filter);
	public Stream<ProjectHolder> getProjectHolderStream(AONContext ctx, ProjectHolderFilter filter);
	public List<ProjectHolder> getProjectHolderList(AONContext ctx, ProjectHolderFilter filter);
	public ProjectHolder saveProjectHolder(AONContext ctx, ProjectHolder holder);
	public void deleteProjectHolder(AONContext ctx, Integer id);
	
	// ---------- PROJECT ACTIVITY
	public Stream<ProjectActivity> getProjectActivityStream(AONContext ctx, ProjectActivityFilter filter);
	public ProjectActivity saveProjectActivity(AONContext ctx, ProjectActivity projectActivity);
	public void deleteProjectActivity(AONContext ctx, Integer id);
}
