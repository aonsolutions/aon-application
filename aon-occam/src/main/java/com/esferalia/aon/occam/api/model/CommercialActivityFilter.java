package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.CommercialActivityProperties;

@FunctionalInterface
public interface CommercialActivityFilter{
	
	Filter filter(CommercialActivityProperties properties);

}
