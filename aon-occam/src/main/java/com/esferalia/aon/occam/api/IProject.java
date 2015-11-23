package com.esferalia.aon.occam.api;

import com.esferalia.aon.occam.api.model.project.ProjectReservation;

public interface IProject {

	public ProjectReservation getProjectReservation(AONContext ctx, Integer projectId);

}
