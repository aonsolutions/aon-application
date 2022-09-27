package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

public interface IConsole {
	
	public String[] getSchemaNames(AONContext ctx);
	public Stream<Domain> getDomains(CloseableAONContext ctx, DomainParams params);
	public boolean deleteDomain(ConsoleParams params);
	public Domain changeActive(CloseableAONContext ctx, Integer domainId, boolean active);
	public Domain changeExpirationDate(CloseableAONContext ctx, Integer domainId, Date expireDate);		
	
}
