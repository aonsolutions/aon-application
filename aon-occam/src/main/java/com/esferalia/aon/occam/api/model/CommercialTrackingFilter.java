package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.CommercialTrackingProperties;

@FunctionalInterface
public interface CommercialTrackingFilter{
	
	Filter filter(CommercialTrackingProperties properties);

}
