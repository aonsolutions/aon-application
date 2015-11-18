package com.esferalia.aon.occam.api.model;


@FunctionalInterface
public interface AttachFilter{
	
	Filter filter(AttachProperties properties);

}
