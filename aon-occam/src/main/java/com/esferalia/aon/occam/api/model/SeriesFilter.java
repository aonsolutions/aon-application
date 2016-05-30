package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.SeriesProperties;

@FunctionalInterface
public interface SeriesFilter{
	
	Filter filter(SeriesProperties properties);

}
