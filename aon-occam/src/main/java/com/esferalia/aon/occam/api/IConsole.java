package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;

public interface IConsole {
	
	public String[] getSchemaNames(AONContext ctx);
	public LinkedList<Domain> getDomains(CloseableAONContext ctx, DomainParams params);		
	
}
