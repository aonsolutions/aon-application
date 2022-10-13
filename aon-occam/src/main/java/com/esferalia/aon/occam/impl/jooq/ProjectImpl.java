package com.esferalia.aon.occam.impl.jooq;

import java.util.List;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProject;
import com.esferalia.aon.occam.api.model.Filter.ProjectCommercialFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectHolderFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectReservationFilter;
import com.esferalia.aon.occam.api.model.Filter.ProjectTypeFilter;
import com.esferalia.aon.occam.api.model.ProjectFilter;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.api.model.project.ProjectType;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectHolderDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectTypeDAO;

public class ProjectImpl implements IProject{
	
	// ------------------------------------- PROJECT
	
	@Override
	public Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getStream(ctx, filter));
	}
	
	@Override
	public Stream<Project> getProjectStream(AONContext ctx, ProjectFilter filter, Integer page, Integer perPage) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getStream(ctx, filter, page, perPage));
	}
	
	@Override
	public Project saveProject(AONContext ctx, Project project) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.save(ctx, project));
	}
	
	@Override
	public void deleteProject(AONContext ctx, Integer projectId) {
		ctx.getDslContext().transaction(
				configuration -> ProjectDAO.delete(ctx, projectId));
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

	// ------------------------------------- PROJECT COMMERCIAL

	@Override
	public Stream<ProjectCommercial> getProjectCommercialStream(AONContext ctx, ProjectCommercialFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getProjectCommercialStream(ctx, filter));
	}

	@Override
	public Integer insertProjectCommercial(AONContext ctx, ProjectCommercial pc) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.insertProjectCommercial(ctx, pc));
	}

	@Override
	public void fixProjectCommercial(AONContext ctx) {
		ctx.getDslContext().transaction(configuration -> ProjectDAO.fixProjectCommercial(ctx));
	}

	// --------- PROJECT TYPE	

	@Override
	public Stream<ProjectType> getProjectTypeStream(AONContext ctx, ProjectTypeFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> ProjectTypeDAO.getStream(ctx, filter));
	}
	
	@Override
	public ProjectType getProjectType(AONContext ctx, ProjectTypeFilter filter) {
		return ctx.getDslContext().transactionResult(
				configuration -> ProjectTypeDAO.get(ctx, filter));
	}
	
	@Override
	public ProjectType saveProjectType(AONContext ctx, ProjectType projectType) {
		return ctx.getDslContext().transactionResult(
				configuration -> ProjectTypeDAO.save(ctx, projectType));
	}
	
	@Override
	public void deleteProjectType(AONContext ctx, Integer id) {
		ctx.getDslContext().transaction(
				configuration -> ProjectTypeDAO.delete(ctx, f ->
					f.getDomainProperty().eq(ctx.getDomainId())
					.and(f.getIdProperty().eq(id))));
	}
	
	
	// --------- PROJECT HOLDER
	
	@Override
	public ProjectHolder getProjectHolder(AONContext ctx, ProjectHolderFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectHolderDAO.get(ctx, filter));
	}

	@Override
	public Stream<ProjectHolder> getProjectHolderStream(AONContext ctx, ProjectHolderFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectHolderDAO.getStream(ctx, filter));
	}

	@Override
	public List<ProjectHolder> getProjectHolderList(AONContext ctx, ProjectHolderFilter filter) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectHolderDAO.getList(ctx, filter));
	}

	@Override
	public ProjectHolder saveProjectHolder(AONContext ctx, ProjectHolder holder) {
		return ctx.getDslContext().transactionResult(configuration -> ProjectHolderDAO.save(ctx, holder));
	}
	
	
	@Override
	public void deleteProjectHolder(AONContext ctx, Integer holderId) {
		ctx.getDslContext().transaction(
				configuration -> ProjectHolderDAO.delete(ctx, holderId));
	}
}
