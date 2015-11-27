package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.AttachProperties;

@FunctionalInterface
public interface AttachFilter{
	
	Filter filter(AttachProperties properties);

}
