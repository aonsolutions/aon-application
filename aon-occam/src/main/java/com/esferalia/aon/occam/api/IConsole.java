package com.esferalia.aon.occam.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

public interface IConsole {
	
	public String[] getSchemaNames(AONContext ctx);
	public LinkedList<Domain> getDomains(CloseableAONContext ctx, DomainParams params);
	public boolean deleteDomain(ConsoleParams params);
	public Domain changeActive(CloseableAONContext ctx, Domain domain);
	public Domain changeExpirationDate(CloseableAONContext ctx, Domain domain);		
	
}
