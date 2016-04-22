package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface AccountingRegistryFilter{
	
	Filter filter(AccountingRegistryProperties properties);

}
