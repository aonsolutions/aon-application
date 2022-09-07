package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;

public interface IConsole {
	
	public String[] getSchemaNames(AONContext ctx);
	public LinkedList<Domain> getDomains(CloseableAONContext ctx, String query);		
	
}
