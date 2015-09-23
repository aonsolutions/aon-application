package com.esferalia.aon.occam.api.model.accounting;

import com.esferalia.aon.occam.api.model.Filter;


@FunctionalInterface
public interface AccountEntryFilter{
	
	Filter filter(AccountEntryProperties properties);

}
