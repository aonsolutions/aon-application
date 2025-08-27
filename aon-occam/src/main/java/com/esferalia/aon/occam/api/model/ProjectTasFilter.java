package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.ProjectTasProperties;

@FunctionalInterface
public interface ProjectTasFilter{
	
	Filter filter(ProjectTasProperties properties);

}
