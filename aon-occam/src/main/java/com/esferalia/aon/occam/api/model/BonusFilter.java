package com.esferalia.aon.occam.api.model;


@FunctionalInterface
public interface BonusFilter{
	
	Filter filter(BonusProperties properties);

}
