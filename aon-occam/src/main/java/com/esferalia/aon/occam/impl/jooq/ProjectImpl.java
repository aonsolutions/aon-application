package com.esferalia.aon.occam.impl.jooq;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IProject;
import com.esferalia.aon.occam.api.model.project.ProjectReservation;
import com.esferalia.aon.occam.impl.jooq.dao.ProjectDAO;

public class ProjectImpl implements IProject{
	
	// ------------------------------------- PROJECT RESERVATION
	
	@Override
	public ProjectReservation getProjectReservation(AONContext ctx, Integer productId) {
		return 	ctx.getDslContext().transactionResult(
				configuration -> ProjectDAO.getProjectReservation(ctx, productId));
	}
	
	
}
