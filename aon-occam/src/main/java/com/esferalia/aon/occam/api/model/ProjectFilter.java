package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;

@FunctionalInterface
public interface ProjectFilter{
	
	Filter filter(ProjectProperties properties);

}
