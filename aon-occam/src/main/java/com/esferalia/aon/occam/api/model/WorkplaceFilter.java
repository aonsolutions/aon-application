package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.WorkplaceProperties;

@FunctionalInterface
public interface WorkplaceFilter{
	
	Filter filter(WorkplaceProperties properties);

}
