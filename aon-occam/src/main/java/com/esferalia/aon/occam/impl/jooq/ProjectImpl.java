package com.esferalia.aon.occam.impl.jooq;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProject;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;

public class ProjectImpl implements IProject{
	
	// ------------------------------------- PROJECT
	
	@Override
	public Project getProject(AONContext ctx, ProjectFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getProject(ctx, filter));
	}
	
	@Override
	public LinkedList<Project> getProjectList(AONContext ctx, ProjectFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getProjectList(ctx, filter));
	}
	
	@Override
	public Integer insertProject(AONContext ctx, Project project) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.insertProject(ctx, project));
	}
	
	// ------------------------------------- PROJECT RESERVATION
	
	@Override
	public ProjectReservation getProjectReservation(AONContext ctx, ProjectReservationFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getProjectReservation(ctx, filter));
	}
	
	@Override
	public Stream<ProjectReservation> getProjectReservationStream(AONContext ctx, ProjectReservationFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getProjectReservationStream(ctx, filter));
	}
	
	
}
