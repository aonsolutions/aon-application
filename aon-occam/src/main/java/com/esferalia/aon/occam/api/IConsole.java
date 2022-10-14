package com.esferalia.aon.occam.api;

import java.util.Date;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.ConsoleDomain;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessage;
import com.esferalia.aon.occam.api.model.console.ConsoleTableRow;
import com.esferalia.aon.occam.impl.jooq.console.ConsoleParams;

public interface IConsole {
	
	public String[] getSchemaNames(AONContext ctx);
	public Stream<ConsoleDomain> getDomains(CloseableAONContext ctx, DomainParams params);
	public boolean deleteDomain(ConsoleParams params);
	public Domain changeActive(CloseableAONContext ctx, Integer domainId, boolean active);
	public Domain changeExpirationDate(CloseableAONContext ctx, Integer domainId, Date expireDate);
	public String remoteAccess(CloseableAONContext ctx, Integer domainId);
	public Boolean fix(CloseableAONContext ctx, ConsoleDomainMessage consoleMessage);
	public ConsoleTableRow viewRow(CloseableAONContext ctx, String schema, String tableName, Integer id);		
	
}
